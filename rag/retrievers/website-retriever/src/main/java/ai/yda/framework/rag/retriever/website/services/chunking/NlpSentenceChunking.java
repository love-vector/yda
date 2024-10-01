package ai.yda.framework.rag.retriever.website.services.chunking;

import ai.yda.framework.rag.core.retriever.ChunkStrategy;
import opennlp.tools.sentdetect.SentenceDetectorME;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.List;

public class NlpSentenceChunking implements ChunkStrategy {

    private final SentenceDetectorME sentenceDetector;

    public NlpSentenceChunking(final @NonNull SentenceDetectorME sentenceDetector) {
        this.sentenceDetector = sentenceDetector;
    }

    @Override
    public List<String> splitChunks(final String text) {
        String[] sentences = sentenceDetector.sentDetect(text);
        return Arrays.asList(sentences);
    }
}
