package dim.kal.com.exceptionMapper;

import dim.kal.com.model.ErrorMessage;
import dim.kal.com.model.LlmRuntimeException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class LlmRuntimeExceptionMapper implements ExceptionMapper<LlmRuntimeException> {
    @Override
    public Response toResponse(LlmRuntimeException exception) {
        ErrorMessage errorMessage = new ErrorMessage(exception.getMessage());

        return Response.status(exception.getStatusCode())
                .entity(errorMessage)
                .build();
    }
}
