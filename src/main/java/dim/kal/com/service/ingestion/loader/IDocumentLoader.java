package dim.kal.com.service.ingestion.loader;

import dim.kal.com.model.Document;
import dim.kal.com.service.ingestion.DocumentType;

import java.util.List;
import java.util.Map;

public interface IDocumentLoader {
    boolean supports(DocumentType type);
    List<Document> load(String source, Map<String,String> params);
}
