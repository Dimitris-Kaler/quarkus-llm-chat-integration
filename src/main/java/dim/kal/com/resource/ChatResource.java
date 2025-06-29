package dim.kal.com.resource;

import dim.kal.com.model.LlmRuntimeException;
import dim.kal.com.model.ResponseMessage;
import dim.kal.com.service.ChatService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/chat")
public class ChatResource {
    @Inject
    ChatService chatService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response chat(
            @QueryParam("message") String message,
            @QueryParam("model") String model) {

        try {
            String reply = chatService.chat(message,model);
            return Response.ok(new ResponseMessage(reply)).build();
        }catch(Exception e){
            throw new LlmRuntimeException(e.getMessage(),Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}