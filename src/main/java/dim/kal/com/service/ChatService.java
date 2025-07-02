package dim.kal.com.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dim.kal.com.client.LlmClient;
import dim.kal.com.validators.IValidatorUtils;
import dim.kal.com.validators.ValidatorUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ChatService {

    @Inject
    LlmClient llmClient;

    @Inject
    ConversationService conversationService;

    @Inject
    IValidatorUtils validatorUtils;

    public String chat(String message,String modelName,String sessionId) {
        validatorUtils.validateMessage(message);
        validatorUtils.validateModelName(modelName);
        conversationService.addMessage(sessionId, message, true);

        // Δημιουργία πλήρους prompt με ιστορικό
        String history = conversationService.getHistory(sessionId);
        String fullPrompt = history + "\nAI:";

        // Κλήση AI
        String response = llmClient.chat(fullPrompt, modelName);

        // Προσθήκη απάντησης AI στο ιστορικό
        conversationService.addMessage(sessionId, response, false);

        return response;

    }
}
