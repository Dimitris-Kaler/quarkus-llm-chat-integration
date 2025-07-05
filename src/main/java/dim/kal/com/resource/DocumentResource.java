package dim.kal.com.resource;

import dim.kal.com.service.DocumentType;
import dim.kal.com.service.RagService;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("api/documents")
public class DocumentResource {

    @Inject
    RagService ragService;

    @POST
    @Path("/ingest")
    public Response ingestDocument(@QueryParam("source") String source) {
        DocumentType type = detectTypeFromSource(source);
        ragService.ingestDocument(source,type, Map.of());
        return Response.ok().build();
    }

    // Νέα μέθοδος για βάσεις δεδομένων
    @POST
    @Path("/ingest-db")
    public Response ingestDatabase(
            @QueryParam("table") String tableName,
            @QueryParam("query") String customQuery) {

        Map<String, String> params = Map.of("query", customQuery); // Προαιρετικό custom query
        ragService.ingestDocument(tableName, DocumentType.DATABASE_JDBC, params);
        return Response.ok().build();
    }

    // Βοηθητικές μέθοδοι
    private DocumentType detectTypeFromSource(String source) {
        if (source.endsWith(".pdf")) return DocumentType.PDF;
        if (source.endsWith(".txt")) return DocumentType.TXT;
        if (source.endsWith(".csv")) return DocumentType.CSV;
        throw new IllegalArgumentException("Unsupported file type");
    }

//    private Map<String, String> parseParams(String json) {
//        // Απλή υλοποίηση (χρησιμοποίησε μια βιβλιοθήκη JSON για production)
//        return json != null ? JsonbBuilder.create().fromJson(json, Map.class) : Map.of();
//    }
}
