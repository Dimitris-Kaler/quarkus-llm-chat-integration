package dim.kal.com.service;

import dev.langchain4j.data.document.DocumentLoader;
import dim.kal.com.model.Document;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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
    IDocumentLoaderService documentLoader;

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


    /**
     * Φόρτωση εγγράφου, chunking και αποθήκευση embeddings.
     */
    public void ingestDocument(String source) {
        List<Document> documents = documentLoader.load(source);
        documents.forEach(doc -> {
            List<String> chunks = textSplitter.split(doc.getContent(), chunkSize, chunkOverlap);
            chunks.forEach(chunk -> {
                float[] embedding = embeddingService.generateEmbedding(chunk);
                vectorService.upsert("documents",
                        new IVectorService.VectorEmbedding(
                                embedding,
                                UUID.randomUUID().toString(),
                                Map.of(
                                        "text", chunk,
                                        "source", doc.getSource()
                                )
                        )
                );
            });
        });
    }

    public List<String> retrieveRelevantChunks(String query, int topK) {
        float[] queryEmbedding = embeddingService.generateEmbedding(query);
        List<IVectorService.VectorMatch> results = vectorService.search(
                "documents",
                queryEmbedding,
                topK,
                null
        );

        return results.stream()
                .map(match -> match.metadata().get("text"))
                .collect(Collectors.toList());
    }
}