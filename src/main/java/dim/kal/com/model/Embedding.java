package dim.kal.com.model;

import java.util.Map;

public class Embedding {
    private String id;
    private float[] vector;
    private Map<String, String> metadata;


    public Embedding(String id, float[] vector, Map<String, String> metadata) {
        this.id = id;
        this.vector = vector;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float[] getVector() {
        return vector;
    }

    public void setVector(float[] vector) {
        this.vector = vector;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
