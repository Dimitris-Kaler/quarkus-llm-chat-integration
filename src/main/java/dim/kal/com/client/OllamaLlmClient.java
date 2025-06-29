package dim.kal.com.client;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class OllamaLlmClient implements LlmClient{


    @ConfigProperty(name = "ollama.base-url" , defaultValue =  "http://localhost:11434")
    String baseUrl;

    @ConfigProperty(name = "ollama.model-name" , defaultValue = "deepseek-llm")
    String defaultModel;

    @ConfigProperty(name = "ollama.temperature" , defaultValue = "0.7")
    double temperature;



    @Override
    public String chat(String message,String modelName) {
        String modelToUse = (modelName != null && !modelName.isEmpty())?modelName:defaultModel;

        ChatLanguageModel chatModel = OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelToUse)  // Δυναμικό μοντέλο
                .temperature(temperature)
                .build();



        return chatModel.generate(message);
    }
}
