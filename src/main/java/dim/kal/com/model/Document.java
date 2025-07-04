package dim.kal.com.model;

import java.util.Map;

public class Document {
    private String id;
    private String content;
    private String source;
    private Map<String,String> metadata;

    public Document(String id, String content, String source, Map<String, String> metadata) {
        this.id = id;
        this.content = content;
        this.source = source;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
