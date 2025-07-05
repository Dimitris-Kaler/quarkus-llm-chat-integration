package dim.kal.com.service;

import dim.kal.com.exception.LlmRuntimeException;
import dim.kal.com.model.Document;
import dim.kal.com.service.ingestion.DocumentType;
import dim.kal.com.service.ingestion.loader.IDocumentLoader;
import dim.kal.com.service.ingestion.ITextSplitterService;
import dim.kal.com.service.vector.EmbeddingService;
import dim.kal.com.service.vector.IVectorService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Core RAG service - συντονίζει document ingestion και retrieval.
 * Συνδέει:
 * - Document loaders
 * - Text splitters
 * - Embedding models
 * - Vector stores
 */
@ApplicationScoped
public class RagService {

    @Inject
    Instance<IDocumentLoader> loaders;
//    @Inject
//    IDocumentLoaderService documentLoader;

    @Inject
    ITextSplitterService textSplitter;

    @Inject
    EmbeddingService embeddingService;

    @Inject
    IVectorService vectorService;

    @ConfigProperty(name = "rag.chunk.size")
    int chunkSize;

    @ConfigProperty(name = "rag.chunk.overlap")
    int chunkOverlap;

    @ConfigProperty(name = "rag.collection.name", defaultValue = "documents")
    String collectionName;

    @ConfigProperty(name = "rag.vector.dimension", defaultValue = "1536")
    int vectorDimension;

    @PostConstruct
    public void init() {
        // Δημιούργησε το collection κατά την εκκίνηση
        ensureCollectionExists(collectionName);
    }

    /**
     * Ελέγχει αν υπάρχει το collection και το δημιουργεί αν χρειάζεται
     */
    private void ensureCollectionExists(String collectionName) {
        try {
            if (!vectorService.collectionExists(collectionName)) {
                System.out.println("Creating collection: " + collectionName);
                vectorService.createCollection(collectionName, vectorDimension);
                System.out.println("Collection created successfully: " + collectionName);
            } else {
                System.out.println("Collection already exists: " + collectionName);
            }
        } catch (Exception e) {
            System.err.println("Failed to ensure collection exists: " + e.getMessage());
            throw new RuntimeException("Failed to initialize vector collection", e);
        }
    }


    /**
     * Φόρτωση εγγράφου, chunking και αποθήκευση embeddings.
     */
    public void ingestDocument(String source, DocumentType type, Map<String, String> params) {
        try {
            // Βεβαιώσου ότι το collection υπάρχει πριν το ingest
            ensureCollectionExists(collectionName);

            IDocumentLoader documentLoader= loaders.stream()
                    .filter(l -> l.supports(type))
                    .findFirst()
                            .orElseThrow(()->new LlmRuntimeException("No loader for type: " + type, Response.Status.INTERNAL_SERVER_ERROR));

            List<Document> documents = documentLoader.load(source,params);
            System.out.println("Loaded " + documents.size() + " documents from: " + source);

            documents.forEach(doc -> {
                List<String> chunks = textSplitter.split(doc.getContent(), chunkSize, chunkOverlap);
                System.out.println("Split document into " + chunks.size() + " chunks");

                chunks.forEach(chunk -> {
                    try {
                        float[] embedding = embeddingService.generateEmbedding(chunk);
                        vectorService.upsert(collectionName,
                                new IVectorService.VectorEmbedding(
                                        embedding,
                                        UUID.randomUUID().toString(),
                                        Map.of(
                                                "text", chunk,
                                                "source", doc.getSource()
                                        )
                                )
                        );
                    } catch (Exception e) {
                        System.err.println("Failed to process chunk: " + e.getMessage());
                        throw new RuntimeException("Failed to process chunk", e);
                    }
                });
            });

            System.out.println("Successfully ingested document: " + source);

        } catch (Exception e) {
            System.err.println("Document ingestion failed: " + e.getMessage());
            throw new RuntimeException("Failed to ingest document: " + source, e);
        }
    }

    public List<String> retrieveRelevantChunks(String query, int topK) {
        try {
            // Βεβαιώσου ότι το collection υπάρχει πριν το search
            ensureCollectionExists(collectionName);

            float[] queryEmbedding = embeddingService.generateEmbedding(query);
            List<IVectorService.VectorMatch> results = vectorService.search(
                    collectionName,
                    queryEmbedding,
                    topK,
                    null
            );

            return results.stream()
                    .map(match -> match.metadata().get("text"))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Failed to retrieve relevant chunks: " + e.getMessage());
            return List.of(); // Επιστρέφει κενή λίστα σε περίπτωση λάθους
        }
    }
}