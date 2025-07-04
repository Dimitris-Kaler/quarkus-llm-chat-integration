package dim.kal.com.client;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dim.kal.com.exception.LlmRuntimeException;
import dim.kal.com.service.OllamaService;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class OllamaLlmClient implements LlmClient{

    private final ConcurrentHashMap<String,ChatLanguageModel> modelCache = new ConcurrentHashMap<>();

    @Inject
    OllamaService ollamaService;


    @ConfigProperty(name = "ollama.base-url" , defaultValue =  "http://localhost:11434")
    String baseUrl;

    @ConfigProperty(name = "ollama.model-name" , defaultValue = "deepseek-llm")
    String defaultModel;

    @ConfigProperty(name = "ollama.temperature" , defaultValue = "0.7")
    double temperature;



    @Override
    public String chat(String message,String modelName) {
        String modelToUse = (modelName != null && !modelName.isEmpty())?modelName:defaultModel;
        ChatLanguageModel model = modelCache.computeIfAbsent(modelToUse, k ->{
            try{
                if (!ollamaService.isModelAvaliable(modelToUse)) {
                    ollamaService.pullModel(modelToUse);
                }
                return buildModel(modelToUse);
            }catch(IOException e){
                throw new LlmRuntimeException("Failed to load model: " + modelToUse+" ,"+e.getMessage(), Response.Status.BAD_REQUEST);
            }
        });


//        ChatLanguageModel chatModel = OllamaChatModel.builder()
//                .baseUrl(baseUrl)
//                .modelName(modelToUse)  // Δυναμικό μοντέλο
//                .temperature(temperature)
//                .build();



        return model.generate(message);
    }

    private ChatLanguageModel buildModel(String modelName){
         ChatLanguageModel chatModel = OllamaChatModel.builder()
               .baseUrl(baseUrl)
                .modelName(modelName)  // Δυναμικό μοντέλο
                .temperature(temperature)
                .timeout(Duration.ofSeconds(50))
                .build();

         return chatModel;
    }

    @PreDestroy
    void cleanup() {
        modelCache.clear();
    }
}
