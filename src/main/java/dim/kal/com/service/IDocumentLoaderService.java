package dim.kal.com.service;

import dim.kal.com.model.Document;

import java.util.List;

public interface IDocumentLoaderService {
    List<Document> load(String source);
}
