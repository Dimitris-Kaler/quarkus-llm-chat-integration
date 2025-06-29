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
    IValidatorUtils validatorUtils;

    public String chat(String message,String modelName) {
        validatorUtils.validateMessage(message);
        validatorUtils.validateModelName(modelName);
        return llmClient.chat(message,modelName);

    }
}
