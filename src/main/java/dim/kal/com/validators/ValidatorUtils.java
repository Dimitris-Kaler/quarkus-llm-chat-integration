package dim.kal.com.validators;

import dim.kal.com.model.ErrorMessage;
import dim.kal.com.model.LlmRuntimeException;
import dim.kal.com.model.OllamaModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class ValidatorUtils implements IValidatorUtils {


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
        if(!OllamaModel.isValidModel(modelName)){
            throw new LlmRuntimeException(
                    String.format(
                            "Unsupported model: '%s'. Available models: %s",
                            modelName,
                            OllamaModel.getSupportedModels()
                    ),Response.Status.BAD_REQUEST
            );
        }
        }

    }

