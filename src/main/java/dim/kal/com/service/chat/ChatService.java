package dim.kal.com.service.chat;

import dim.kal.com.client.LlmClient;
import dim.kal.com.service.RagService;
import dim.kal.com.validators.IValidatorUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@ApplicationScoped
public class ChatService {

    @Inject
    LlmClient llmClient;

    @Inject
    ConversationService conversationService;

    @Inject
    IValidatorUtils validatorUtils;


    @Inject
    RagService ragService;

    @ConfigProperty(name = "chat.use-rag", defaultValue = "false")
    boolean useRag;

    public String chat(String message,String modelName,String sessionId) {
        validatorUtils.validateMessage(message);
        validatorUtils.validateModelName(modelName);
        conversationService.addMessage(sessionId, message, true);

        String response;

        if(useRag){
            List<String> relevantChunks = ragService.retrieveRelevantChunks(message,3);
            String context = String.join("\n---\n", relevantChunks);
            String ragPrompt = String.format(
                    "Context:\n%s\n\nQuestion: %s",
                    context,
                    conversationService.getHistory(sessionId)
            );
            response = llmClient.chat(ragPrompt, modelName);
        }else{
            response = llmClient.chat(conversationService.getHistory(sessionId), modelName);
        }
        conversationService.addMessage(sessionId, response, false);
        return response;
//        // Δημιουργία πλήρους prompt με ιστορικό
//        String history = conversationService.getHistory(sessionId);
//        String fullPrompt = history + "\nAI:";
//
//        // Κλήση AI
//        String response = llmClient.chat(fullPrompt, modelName);
//
//        // Προσθήκη απάντησης AI στο ιστορικό
//        conversationService.addMessage(sessionId, response, false);
//
//        return response;

    }
}
