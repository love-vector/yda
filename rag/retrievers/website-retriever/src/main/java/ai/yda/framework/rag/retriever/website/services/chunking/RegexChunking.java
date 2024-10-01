package ai.yda.framework.rag.retriever.website.services.chunking;

import ai.yda.framework.rag.core.retriever.ChunkStrategy;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegexChunking implements ChunkStrategy {
    private final List<String> patterns;

    public RegexChunking(final @NonNull List<String> patterns) {
        if (patterns.isEmpty()) {
            this.patterns = List.of("\\n\\n");
        } else {
            this.patterns = patterns;
        }
    }

    @Override
    public List<String> splitChunks(final String text) {
        List<String> paragraphs = new ArrayList<>();
        paragraphs.add(text);

        for (String pattern : patterns) {
            List<String> newParagraphs = new ArrayList<>();
            for (String paragraph : paragraphs) {
                var splitParagraphs = paragraph.split(pattern);
                newParagraphs.addAll(Arrays.asList(splitParagraphs));
            }
            paragraphs = newParagraphs;
        }

        return paragraphs;
    }
}
