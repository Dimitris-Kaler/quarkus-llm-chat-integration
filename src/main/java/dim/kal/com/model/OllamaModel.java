package dim.kal.com.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum OllamaModel {
    DEEPSEEK_LLM("deepseek-llm"),
    MISTRAL("mistral"),
    LLAMA3("llama3");

    private final String modelName;

    OllamaModel(String modelName){
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }

    public static boolean isValidModel(String modelName) {
        for (OllamaModel model : values()) {
            if (model.getModelName().equalsIgnoreCase(modelName)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getSupportedModels(){
      return  Arrays.stream(values())
                .map(OllamaModel::getModelName)
                .collect(Collectors.toList());
    }
}
