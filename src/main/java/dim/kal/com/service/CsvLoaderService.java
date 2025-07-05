package dim.kal.com.service;

import dim.kal.com.exception.DataLoadingException;
import dim.kal.com.model.Document;
import java.util.HashMap;
import java.io.FileReader;
import java.util.ArrayList;
import com.opencsv.CSVReader;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CsvLoaderService implements IDocumentLoaderService{
    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.CSV;
    }

    @Override
    public List<Document> load(String filePath, Map<String, String> params) throws DataLoadingException {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {

            // Παράμετροι (default: comma-delimited)
            String delimiter = params.getOrDefault("delimiter", ",");
            boolean hasHeaders = Boolean.parseBoolean(params.getOrDefault("headers", "true"));

            List<Document> docs = new ArrayList<>();
            String[] headers = hasHeaders ? reader.readNext() : null;

            String[] line;
            while ((line = reader.readNext()) != null) {
                docs.add(createDocument(line, headers, filePath));
            }
            return docs;

        } catch (Exception e) {
            throw new DataLoadingException("Failed to load CSV: " + filePath, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private Document createDocument(String[] row, String[] headers, String source) {
        StringBuilder content = new StringBuilder();
        Map<String, String> metadata = new HashMap<>();

        for (int i = 0; i < row.length; i++) {
            String header = (headers != null && i < headers.length) ? headers[i] : "col_" + i;
            content.append(header).append(": ").append(row[i]).append("\n");
            metadata.put(header, row[i]);
        }

        return new Document(
                UUID.randomUUID().toString(),
                content.toString(),
                source,
                metadata
        );
    }
}
