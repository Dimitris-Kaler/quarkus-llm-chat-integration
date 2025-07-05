package dim.kal.com.service.vector;

import java.util.List;
import java.util.Map;


/**
 * Διασύνδεση για διαχείριση Vector Store (Qdrant, Pinecone, κλπ).
 * Υπεύθυνη για CRUD operations σε vector embeddings και similarity search.
 */
public interface IVectorService {

    /**
     * Δημιουργεί νέα συλλογή (collection) για αποθήκευση vectors.
     * @param name Όνομα συλλογής
     * @param dimensions Διαστάσεις των vectors (π.χ. 384 για το all-minilm)
     */
    void createCollection(String name, int dimensions);

    /**
     * Ελέγχει αν υπάρχει συλλογή.
     * @param name Όνομα συλλογής
     * @return true αν υπάρχει
     */
    boolean collectionExists(String name);


    void deleteCollection(String name);

    /**
     * Προσθήκη/Ενημέρωση ενός vector embedding.
     * @param collectionName Όνομα συλλογής
     * @param embedding Το embedding (διάνυσμα + μεταδεδομένα)
     */
    void upsert(String collectionName, VectorEmbedding embedding);
    void upsertBulk(String collectionName, List<VectorEmbedding> embeddings);

    /**
     * Αναζήτηση με βάση similarity.
     * @param collectionName Όνομα συλλογής
     * @param vector Query vector
     * @param topK Πλήθος αποτελεσμάτων
     * @param filters Φίλτρα αναζήτησης (π.χ. {"category": "legal"})
     * @return Λίστα ταιριάζοντων vectors με score
     */
    List<VectorMatch> search(
            String collectionName,
            float[] vector,
            int topK,
            Map<String, String> filters
    );

    /**
     * Embedding record για αποθήκευση.
     */
    record VectorEmbedding(
            float[] vector,
            String id,
            Map<String, String> metadata
    ) {}


    /**
     * Αποτέλεσμα αναζήτησης.
     */
    record VectorMatch(
            String id,
            float score,
            Map<String, String> metadata
    ) {}

}
