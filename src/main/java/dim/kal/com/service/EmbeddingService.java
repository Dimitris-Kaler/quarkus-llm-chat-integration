package dim.kal.com.service;


import dev.langchain4j.data.embedding.Embedding;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import dev.langchain4j.model.embedding.EmbeddingModel;
/**
 * Διαχείριση text embeddings χρησιμοποιώντας Ollama.
 * Μετατρέπει κείμενο σε διανύσματα για vector store.
 */
@ApplicationScoped
public class EmbeddingService {
    @Inject
    OllamaService ollamService;
    @Inject
    EmbeddingModel embeddingModel;  // Αυτόματη ενσωμάτωση από LangChain4J

    @ConfigProperty(name = "ollama.embedding-model", defaultValue = "all-minilm")
    String modelName;

    /**
     * Μετατρέπει κείμενο σε embedding διάνυσμα.
     *
     * @param text Το κείμενο προς μετατροπή
     * @return Το embedding διάνυσμα (συνήθως 384 ή 768 διαστάσεις)
     * @throws RuntimeException Αν αποτύχει η δημιουργία embedding
     */
    public float[] generateEmbedding(String text) {
        try {
            // 1. Δημιουργία embedding
            Embedding embedding = embeddingModel.embed(text).content();

            // 2. Μετατροπή σε float[]
            return embedding.vector();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate embedding for text: " + text, e);
        }
    }
}
