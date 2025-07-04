package dim.kal.com.resource;

import dim.kal.com.service.RagService;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("api/documents")
public class DocumentResource {

    @Inject
    RagService ragService;

    @POST
    @Path("/ingest")
    public Response ingestDocument(@QueryParam("source") String source) {
        ragService.ingestDocument(source);
        return Response.ok().build();
    }
}
