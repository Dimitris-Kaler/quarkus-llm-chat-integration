# llm-integration-demo

This project shows how to integrate the Ollama LLM with a Quarkus application using Langchain4j.
It demonstrates a simple chat API that sends your messages to Ollama’s deepseek-llm model and returns the response


----

## Requirements

- **Linux** (or WSL on Windows)
- **Docker** (to run the Ollama server)
- **Java 21+**
- **Maven or Gradle** (depending on your build tool)
- **Quarkus 3.x**

---

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Setup Instructions

1. **Run Ollama server in Docker**
   - Start the Ollama server container:
```bash 
   docker run -d -p 11434:11434 --name ollama ollama/ollama
 ```
This runs Ollama and exposes port 11434 on your machine, which your app will connect to.

2. **Download the model inside the container**
 
- Pull the deepseek-llm model to Ollama:
```bash
   docker exec ollama ollama pull deepseek-llm
```

You need this so Ollama can serve that model for your requests.

3. **Verify Ollama Model is loaded**
 
   After starting the container and pulling the model, you can verify that deepseek-llm is properly loaded by accessing:

```bash
http://localhost:11434/api/tags
```

You should get a JSON response like this:
```json
{
  "models": [
    {
      "name": "deepseek-llm:latest",
      "model": "deepseek-llm:latest",
      "modified_at": "2025-06-27T08:52:47.957058691Z",
      "size": 4000473688,
      "digest": "9aab369a853bb12b8187d14eca701385f0a564cd2ae938fb4cbdb31bf2d43fc2",
      "details": {
        "parent_model": "",
        "format": "gguf",
        "family": "llama",
        "families": null,
        "parameter_size": "7B",
        "quantization_level": "Q4_0"
      }
    }
  ]
}

```

**Warning:** The /api/tags endpoint reveals detailed model info and should not be publicly exposed in production environments for security reasons.

----



## Build & Run Your Quarkus App

- **Build with Gradle**

```shell script
./gradlew build
```

- **Run in dev mode**

```shell script
./gradlew quarkusDev

````
The app starts on http://localhost:{quarkus.http.port}, which you configure in your application.properties..


## How to Use the API

Send a GET request with the message parameter:

```bash
curl --location 'http://localhost:8081/chat?message=%22Hello%22'

```
Example JSON response from the LLM:

```bash
{
    "message": "Hi! How can I help you today?"
}
```


## **Application.properties** explanation
```properties
quarkus.http.port=8081

```
- Sets the HTTP server port for the Quarkus application to listen on.

- By default Quarkus runs on 8080, here you changed it to 8081.

```properties
# LLM CONFIGURATION
ollama.base-url=http://localhost:11434
ollama.model-name=deepseek-llm
ollama.temperature=0.7

```
- These three properties configure your Ollama LLM client:

    - **ollama.base-url**: The URL where the Ollama Docker server is running.

**ollama.model-name**: The name of the model you want to use (deepseek-llm).

**ollama.temperature**: Controls randomness in generation; 0.7 is moderate creativity.

In your LlmConfig class, you can inject those values using the `@ConfiProperty` annotation:

```java
@ConfigProperty(name = "ollama.base-url")
String baseUrl;

@ConfigProperty(name = "ollama.model-name")
String modelName;

@ConfigProperty(name = "ollama.temperature")
double temperature;

```

## Project Code Overview

- **LlmConfig.java**
  Configures and creates the OllamaChatModel bean used to communicate with the Ollama server. 
- It sets the base URL (http://localhost:11434), selects the deepseek-llm model, and defines parameters like temperature for generating responses.



- **ChatService.java**
  This class is responsible for communicating with the Ollama language model. It takes the user’s input message, sends it to the Ollama deepseek-llm model via the Langchain4j API, and returns the generated text response. Any exceptions during this call are propagated up to be handled by the REST layer.

- **ChatResource.java**
  Defines the REST API endpoint /chat that accepts a message query parameter. It validates the input, calls ChatService to get the model's reply, and returns either a JSON success response or an error message if validation fails.

- **ResponseMessage.java & ErrorMessage.java**
Simple POJOs used for JSON responses — one for success messages and one for error messages.

