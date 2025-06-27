package dim.kal.com.resource;

import dim.kal.com.model.ErrorMessage;
import dim.kal.com.model.ResponseMessage;
import dim.kal.com.service.ChatService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/chat")
public class ChatResource {
    @Inject
    ChatService chatService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response chat(@QueryParam("message") String message) {
        if (message == null || message.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMessage("The message is missing")).build();

        }
        try {
            String reply = chatService.chat(message);
            return Response.ok(new ResponseMessage(reply)).build();
        }catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorMessage("Internal error: " + e.getMessage()))
                    .build();
        }
    }
}