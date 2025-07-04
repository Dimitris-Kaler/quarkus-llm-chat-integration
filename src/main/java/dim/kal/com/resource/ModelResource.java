package dim.kal.com.resource;

import dim.kal.com.service.OllamaService;
import dim.kal.com.exception.LlmRuntimeException;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;

@Path("/api/models")
public class ModelResource {
    @Inject
    OllamaService ollamaService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listModels() {
        try {
            return Response.ok(ollamaService.getAvailableModels()).build();
        } catch (IOException e) {
            throw new LlmRuntimeException("Failed to fetch models", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
