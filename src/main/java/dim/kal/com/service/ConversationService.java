package dim.kal.com.service;


import dim.kal.com.model.ChatMessage;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.time.LocalDateTime;
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

        cleanupExpiredConversations();

        List<ChatMessage>messages = conversations.computeIfAbsent(sessionId,k -> new ArrayList<>());
//        conversations.computeIfAbsent(sessionId, k -> new ArrayList<>())
//                .add(new ChatMessage(isUser, message));

        if(messages.size()>=maxMessages){
            messages.remove(0);
        }
        messages.add(new ChatMessage(isUser,message));
    }

    public String getHistory(String sessionId) {

        cleanupExpiredConversations();

        return conversations.getOrDefault(sessionId, List.of()).stream()
                .map(m -> m.isUser() ? "User: " + m.getText() : "AI: " + m.getText())
                .collect(Collectors.joining("\n"));
    }

    private void cleanupExpiredConversations() {
        LocalDateTime now = LocalDateTime.now();
        conversations.entrySet().removeIf(entry -> {
            if (entry.getValue().isEmpty()) {
                return true;
            }
            LocalDateTime lastMessageTime = entry.getValue().get(entry.getValue().size() - 1).getTimestamp();
            return Duration.between(lastMessageTime, now).toMinutes() >= timeoutMinutes;
        });
    }

    public void clearHistory(String sessionId) {
        conversations.remove(sessionId);
    }

}
