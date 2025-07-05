package dim.kal.com.service.ingestion;

import java.util.List;

public interface ITextSplitterService {
    List<String> split(String text, int chunkSize, int overlap);
}
