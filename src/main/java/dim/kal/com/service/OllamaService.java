package dim.kal.com.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.JsonArray;



import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class OllamaService {
    @ConfigProperty(name = "ollama.base-url", defaultValue = "http://localhost:11434")
    String baseUrl;

    private static final Client httpClient = ClientBuilder.newClient();


    public boolean isModelAvaliable(String modelName) throws IOException{
        try{
            Response response =  httpClient.target(baseUrl)
                    .path("/api/tags")
                    .request()
                    .get();

            System.out.println("Response: "+response.getStatus());
            if(response.getStatus() == Response.Status.OK.getStatusCode()){
                String responseBody = response.readEntity(String.class);
                System.out.println("ResponseBody: "+responseBody);
                return responseBody.contains("\"name\":\"" + modelName + "\"");
            }
            return false;

        }catch(Exception e){
            throw new IOException("Failed to check model availability", e);
        }
    }



    public void pullModel(String modelName) throws IOException {

        Response response = httpClient.target(baseUrl)
                .path("/api/pull")
                .request()
                .post(Entity.json( "{ \"name\": \"" + modelName + "\" }"));

        if (response.getStatus() != 200) {
            String errorBody = response.readEntity(String.class);
            throw new IOException("Failed to pull model " + modelName + ": " + errorBody);
        }

        InputStream inputStream = response.readEntity(InputStream.class);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[Ollama Pull] " + line);
                if (line.contains("\"status\":\"success\"")) {
                    return; // Επιτυχής λήψη
                }
                if (line.contains("\"error\"")) {
                    throw new IOException("Error while pulling model: " + line);
                }
            }
        }
    }


    /**
         * Λίστα διαθέσιμων μοντέλων
         */
        public Set<String> getAvailableModels() throws IOException {
            Response response = httpClient.target(baseUrl)
                    .path("/api/tags")
                    .request()
                    .get();

            if (response.getStatus() == 200) {
                JsonObject root = Json.createReader(response.readEntity(InputStream.class))
                        .readObject();
                JsonArray models = root.getJsonArray("models");
                return models.stream()
                        .map(m -> m.asJsonObject().getString("name"))
                        .collect(Collectors.toSet());
            }
            throw new IOException("Failed to fetch models");
        }
    }
