package dim.kal.com.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ChatService {

    @Inject
    ChatLanguageModel chatLanguageModel;

    public String chat(String message) throws Exception {
        return chatLanguageModel.generate(message);

    }
}
