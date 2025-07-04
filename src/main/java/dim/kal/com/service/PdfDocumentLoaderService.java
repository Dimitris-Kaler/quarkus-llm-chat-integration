package dim.kal.com.service;

import dim.kal.com.model.Document;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class PdfDocumentLoaderService implements IDocumentLoaderService{
    @Override
    public List<Document> load(String filePath) {
        try (PDDocument pdfDocument = Loader.loadPDF(new File(filePath))) {  // Σωστή μέθοδος φόρτωσης
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(pdfDocument);

            Document doc = new Document(
                    UUID.randomUUID().toString(),
                    text,
                    filePath,
                    Map.of(
                            "type", "pdf",
                            "pages", String.valueOf(pdfDocument.getNumberOfPages()),
                            "size_kb", String.valueOf(new File(filePath).length() / 1024)
                    )
            );

            pdfDocument.close(); // Κλείσιμο εγγράφου
            return Collections.singletonList(doc);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load PDF: " + e.getMessage(), e);
        }
    }
}
