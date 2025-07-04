package dim.kal.com.service;

import java.util.List;

public interface ITextSplitterService {
    List<String> split(String text, int chunkSize, int overlap);
}
