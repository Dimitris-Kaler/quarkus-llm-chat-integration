package dim.kal.com.service;

import dim.kal.com.exception.DataLoadingException;
import dim.kal.com.model.Document;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
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
public class PdfDocumentLoaderService implements IDocumentLoaderService {
//    @Override
//    public List<Document> load(String filePath) {
//        System.out.println("Current working directory: " + System.getProperty("user.dir"));
//        System.out.println("Class location: " + getClass().getProtectionDomain().getCodeSource().getLocation());
//
//        File file = new File("test");
//        System.out.println("Looking for: " + file);
//        System.out.println("Absolute path: " + file.getAbsolutePath());
//        System.out.println("File exists: " + file.exists());
//
//        try (PDDocument pdfDocument = Loader.loadPDF(new File(filePath))) {  // Σωστή μέθοδος φόρτωσης
//            PDFTextStripper stripper = new PDFTextStripper();
//            String text = stripper.getText(pdfDocument);
//
//            Document doc = new Document(
//                    UUID.randomUUID().toString(),
//                    text,
//                    filePath,
//                    Map.of(
//                            "type", "pdf",
//                            "pages", String.valueOf(pdfDocument.getNumberOfPages()),
//                            "size_kb", String.valueOf(new File(filePath).length() / 1024)
//                    )
//            );
//
//            pdfDocument.close(); // Κλείσιμο εγγράφου
//            return Collections.singletonList(doc);
//
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to load PDF: " + e.getMessage(), e);
//        }
//    }

    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.PDF;
    }

    @Override
    public List<Document> load(String filePath, Map<String, String> params) throws DataLoadingException {

        try (PDDocument pdfDocument = Loader.loadPDF(new File(filePath))) {
            PDFTextStripper stripper = new PDFTextStripper();

            // Επεξεργασία παραμέτρων (προαιρετικό)
            if (params.containsKey("startPage")) {
                stripper.setStartPage(Integer.parseInt(params.get("startPage")));
            }
            if (params.containsKey("endPage")) {
                stripper.setEndPage(Integer.parseInt(params.get("endPage")));
            }

            String text = stripper.getText(pdfDocument);
            return Collections.singletonList(createDocument(filePath, text, pdfDocument));

        } catch (IOException e) {
            throw new DataLoadingException("Failed to load PDF: " + filePath, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private Document createDocument(String filePath, String text, PDDocument pdfDocument) {
        return new Document(
                UUID.randomUUID().toString(),
                text,
                filePath,
                createMetadata(filePath, pdfDocument)
        );
    }

    private Map<String, String> createMetadata(String filePath, PDDocument pdfDocument) {
        return Map.of(
                "type", "pdf",
                "pages", String.valueOf(pdfDocument.getNumberOfPages()),
                "size_kb", String.valueOf(new File(filePath).length() / 1024),
                "encrypted", String.valueOf(pdfDocument.isEncrypted())
        );
    }
}
