package dim.kal.com.exception;

import jakarta.ws.rs.core.Response;

public class DataLoadingException extends  LlmRuntimeException{
    public DataLoadingException(String message, int statusCode) {
        super(message, statusCode);
    }

    public DataLoadingException(String message, Response.Status statusCode) {
        super(message, statusCode);
    }
}
