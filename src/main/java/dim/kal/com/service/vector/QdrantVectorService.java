package dim.kal.com.service.vector;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dim.kal.com.exception.QdrantOperationException;
import dim.kal.com.service.vector.IVectorService;
import jakarta.json.JsonValue;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Υλοποίηση του IVectorService για Qdrant vector database.
 * Χρησιμοποιεί Qdrant REST API για διαχείριση embeddings.
 */

@ApplicationScoped
public class QdrantVectorService implements IVectorService {

    @Inject
    Client httpClient; //  REST client (Quarkus/JAX-RS)

    private static final String QDRANT_BASE_URL = "http://localhost:6333";


    /**
     * Δημιουργεί collection με cosine similarity metric.
     */
    @Override
    public void createCollection(String name, int dimensions) {
        JsonObject body = Json.createObjectBuilder()
                .add("vectors", Json.createObjectBuilder()
                        .add("size",dimensions)
                        .add("distance","Cosine"))
                .build();

        Response response = httpClient.target(QDRANT_BASE_URL)
                .path("/collections/" + name)
                .request()
                .put(Entity.entity(body.toString(), MediaType.APPLICATION_JSON));
    }


    /**
     * Αναζήτηση με vector similarity και optional filters.
     */
    @Override
    public boolean collectionExists(String name) {
        Response response = httpClient.target(QDRANT_BASE_URL)
                .path("/collections/" + name)
                .request()
                .get();

        return response.getStatus() == 200;
    }

    @Override
    public void deleteCollection(String name) {
        Response response = httpClient.target(QDRANT_BASE_URL)
                .path("/collections/" + name)
                .request()
                .delete();

        if (response.getStatus() != 200) {
            throw new QdrantOperationException("Failed to delete collection: " + response.readEntity(String.class),response.getStatus());
        }
    }

    @Override
    public void upsert(String collectionName, VectorEmbedding embedding) {
        upsertBulk(collectionName, List.of(embedding));
    }

    @Override
    public void upsertBulk(String collectionName, List<VectorEmbedding> embeddings) {
        // 1. Δημιουργία του points array
        JsonArrayBuilder pointsArrayBuilder = Json.createArrayBuilder();

        for (VectorEmbedding e : embeddings) {
            JsonArrayBuilder vectorBuilder = Json.createArrayBuilder();
            for (float value : e.vector()) {
                vectorBuilder.add(value);
            }

            pointsArrayBuilder.add(
                    Json.createObjectBuilder()
                            .add("id", e.id())
                            .add("vector", vectorBuilder)
                            .add("payload", Json.createObjectBuilder(e.metadata()))
            );
        }

        // 2. Δημιουργία του τελικού JSON body
        JsonObject body = Json.createObjectBuilder()
                .add("points", pointsArrayBuilder)
                .build();

        // 3. Κλήση του Qdrant API
        Response response = httpClient.target(QDRANT_BASE_URL)
                .path("/collections/" + collectionName + "/points")
                .request()
                .put(Entity.entity(body.toString(), MediaType.APPLICATION_JSON));

        if (response.getStatus() != 200) {
            throw new QdrantOperationException("Failed to upsert vectors: " + response.readEntity(String.class),response.getStatus());
        }
    }


    @Override
    public List<VectorMatch> search(String collectionName,
                                    float[] vector,
                                    int topK,
                                    Map<String, String> filters) {

        // 1. Κατασκευή JSON request
        JsonObjectBuilder requestBuilder = Json.createObjectBuilder()
                .add("vector", vectorToJsonArray(vector))
                .add("top", topK)
                .add("with_payload", true)
                .add("with_vectors", false);

        if (filters != null && !filters.isEmpty()) {
            requestBuilder.add("filter", buildFilter(filters));
        }

        // 2. Κλήση API
        Response response = httpClient.target(QDRANT_BASE_URL)
                .path("/collections/" + collectionName + "/points/search")
                .request()
                .post(Entity.entity(requestBuilder.build().toString(), MediaType.APPLICATION_JSON));

        // 3. Έλεγχος αποτελέσματος
        if (response.getStatus() != 200) {
            throw new QdrantOperationException("Search failed: " + response.readEntity(String.class),response.getStatus());
        }

        // 4. Επεξεργασία response
        try (InputStream is = response.readEntity(InputStream.class);
             JsonReader reader = Json.createReader(is)) {

            JsonArray results = reader.readObject().getJsonArray("result");

            return results.stream()
                    .map(JsonValue::asJsonObject)
                    .map(item -> new VectorMatch(
                            item.getString("id"),
                            (float) item.getJsonNumber("score").doubleValue(),
                            item.getJsonObject("payload").entrySet().stream()
                                    .collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            e -> e.getValue().toString()
                                    ))
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new QdrantOperationException(
                    "Failed to parse search results: " + e.getMessage(),
                    Response.Status.INTERNAL_SERVER_ERROR
            );
        }
    }

    // Helper για μετατροπή float[] → JSON array
    private JsonArray vectorToJsonArray(float[] vector) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (float value : vector) {
            builder.add(value);
        }
        return builder.build();
    }

    // Helper method για δημιουργία φίλτρων
    private JsonObject buildFilter(Map<String, String> filters) {
        JsonObjectBuilder filterBuilder = Json.createObjectBuilder();
        JsonArrayBuilder mustBuilder = Json.createArrayBuilder();

        filters.forEach((key, value) -> {
            mustBuilder.add(
                    Json.createObjectBuilder()
                            .add("key", key)
                            .add("match", Json.createObjectBuilder()
                                    .add("value", value))
            );
        });

        return filterBuilder.add("must", mustBuilder).build();
    }

}