package dim.kal.com.service.ingestion;

import dim.kal.com.service.ingestion.ITextSplitterService;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@ApplicationScoped
public class RecursiveTextSplitterService implements ITextSplitterService {

    private static final Pattern SPLIT_PATTERN = Pattern.compile(
            "(?<=[.!?]\\s)|(?<=\\n)|(?=\\n)|(?<=^\\s{2,})"
    );
    @Override
    public List<String> split(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        String[] sentences = SPLIT_PATTERN.split(text);

        StringBuilder currentChunk = new StringBuilder();
        for (int i = 0; i < sentences.length; i++) {
            if (currentChunk.length() + sentences[i].length() > chunkSize && !currentChunk.isEmpty()) {
                chunks.add(currentChunk.toString());
                currentChunk = new StringBuilder(
                        currentChunk.substring(Math.max(0, currentChunk.length() - overlap))
                );
            }
            currentChunk.append(sentences[i]);
        }

        if (!currentChunk.isEmpty()) {
            chunks.add(currentChunk.toString());
        }

        return chunks;
    }
}
