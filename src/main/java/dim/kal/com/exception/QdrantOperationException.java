package dim.kal.com.exception;

import jakarta.ws.rs.core.Response;

public class QdrantOperationException extends LlmRuntimeException{
    public QdrantOperationException(String message, int statusCode) {
        super(message, statusCode);
    }

    public QdrantOperationException(String message, Response.Status statusCode) {
        super(message, statusCode);
    }
}
