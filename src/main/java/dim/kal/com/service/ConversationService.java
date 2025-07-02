package dim.kal.com.service;


import dim.kal.com.model.ChatMessage;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ApplicationScoped
public class ConversationService {
    @ConfigProperty(name = "conversation.max-messages", defaultValue = "20")
    int maxMessages;

    @ConfigProperty(name = "conversation.timeout-minutes", defaultValue = "30")
    int timeoutMinutes;

    private final Map<String, List<ChatMessage>> conversations = new ConcurrentHashMap<>();

    public void addMessage(String sessionId, String message, boolean isUser) {
        conversations.computeIfAbsent(sessionId, k -> new ArrayList<>())
                .add(new ChatMessage(isUser, message));
    }

    public String getHistory(String sessionId) {
        return conversations.getOrDefault(sessionId, List.of()).stream()
                .map(m -> m.isUser() ? "User: " + m.getText() : "AI: " + m.getText())
                .collect(Collectors.joining("\n"));
    }

    public void clearHistory(String sessionId) {
        conversations.remove(sessionId);
    }

}
