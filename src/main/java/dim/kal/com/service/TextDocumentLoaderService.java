package dim.kal.com.service;

import dim.kal.com.exception.DataLoadingException;
import dim.kal.com.model.Document;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class TextDocumentLoaderService implements IDocumentLoaderService {

    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.TXT;
    }

    @Override
    public List<Document> load(String source, Map<String, String> params) {
        try {
            String content = Files.readString(Paths.get(source));
            return List.of(
                    new Document(
                            UUID.randomUUID().toString(),
                            content,
                            source,
                            Map.of(
                                    "type", "text/plain",
                                    "size", String.valueOf(content.length())
                            )
                    )
            );
        } catch (IOException e) {
            throw new DataLoadingException("Failed to load text file", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}

