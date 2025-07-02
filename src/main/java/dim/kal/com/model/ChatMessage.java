package dim.kal.com.model;

import java.time.LocalDateTime;

public class ChatMessage {
    private final boolean isUser;
    private final String text;
    private final LocalDateTime timestamp;
    private String metadata;

    public ChatMessage(boolean isUser, String text) {
        this(isUser, text, LocalDateTime.now(), null);
    }

    public ChatMessage(boolean isUser, String text,LocalDateTime timestamp,String metadata) {
        this.isUser = isUser;
        this.text = text;
        this.timestamp = timestamp;
        this.metadata = metadata;
    }

    public boolean isUser() { return isUser; }
    public String getText() { return text; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getMetadata() { return metadata; }


    public String toPromptString() {
        return (isUser ? "User: " : "AI: ") + text;
    }
}
