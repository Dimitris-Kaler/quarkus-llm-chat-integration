package dim.kal.com.validators;

import dim.kal.com.exception.LlmRuntimeException;
import dim.kal.com.service.OllamaService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class ValidatorUtils implements IValidatorUtils {

    @Inject
    OllamaService ollamaService;

    @Override
    public void validateMessage(String message) {
        if (message == null || message.isEmpty()) {
                throw new LlmRuntimeException("Message cannot be null or empty",Response.Status.BAD_REQUEST);

        }
    }

    @Override
    public void validateModelName(String modelName) {
        if (modelName == null || modelName.trim().isEmpty()) {
            return;
        }
//        try{
//        if (!ollamaService.isModelAvaliable(modelName)) {
//            throw new LlmRuntimeException(
//                    String.format("Model '%s' not available. Use /api/models to list installed models", modelName),
//                    Response.Status.BAD_REQUEST
//            );
//        }}catch(IOException e){
//            throw new LlmRuntimeException(
//                    "Failed to verify model availability. Please try again later.",
//                    Response.Status.INTERNAL_SERVER_ERROR
//            );
//        }
    }
}
