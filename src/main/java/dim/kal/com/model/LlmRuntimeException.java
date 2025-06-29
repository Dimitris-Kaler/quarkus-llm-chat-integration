package dim.kal.com.model;

import jakarta.ws.rs.core.Response;

public class LlmRuntimeException extends RuntimeException{
    private final int statusCode;

    public LlmRuntimeException(String message,int statusCode){
        super(message);
        this.statusCode = statusCode;
    }

    public LlmRuntimeException(String message, Response.Status statusCode){
        super(message);
        this.statusCode = statusCode.getStatusCode();
    }

    public int getStatusCode() {
        return statusCode;
    }
}
