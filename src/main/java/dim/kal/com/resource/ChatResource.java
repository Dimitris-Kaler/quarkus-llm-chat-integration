package dim.kal.com.resource;

import dim.kal.com.exception.LlmRuntimeException;
import dim.kal.com.model.ResponseMessage;
import dim.kal.com.service.ChatService;
import dim.kal.com.service.ConversationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import java.util.UUID;


@Path("/chat")
public class ChatResource {
    @Inject
    ChatService chatService;

    @Inject
    ConversationService conversationService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response chat(
            @QueryParam("message") String message,
            @QueryParam("model") String model,
            @CookieParam("sessionId") String sessionId) {

        try {
            String reply = chatService.chat(message,model,sessionId);
            return Response.ok(new ResponseMessage(reply)).build();
        }catch(Exception e){
            throw new LlmRuntimeException(e.getMessage(),Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("/new")
    public Response startNewSession() {
        String newSessionId = UUID.randomUUID().toString();
        return Response.ok()
                .cookie(new NewCookie("sessionId", newSessionId))
                .build();
    }


    @GET
    @Path("/history")
    public Response getHistory(@CookieParam("sessionId") String sessionId) {
        return Response.ok(conversationService.getHistory(sessionId)).build();
    }
}