package dim.kal.com.service;

import dim.kal.com.model.Document;

import java.util.List;
import java.util.Map;

public interface IDocumentLoaderService {
    boolean supports(DocumentType type);
    List<Document> load(String source, Map<String,String> params);
}
