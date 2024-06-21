package ai.yda.framework.rag.retriever.filesystem.util;

import java.util.ArrayList;
import java.util.List;

public final class ContentUtil {
    public static List<String> preprocessContent(String content) {
        return List.of(content.trim());
    }

    public static List<String> splitContent(String content, int chunkMaxLength) {
        List<String> chunks = new ArrayList<>();
        int length = content.length();
        for (int i = 0; i < length; i += chunkMaxLength) {
            chunks.add(content.substring(i, Math.min(length, i + chunkMaxLength)));
        }
        return chunks;
    }

    private ContentUtil() {}
}