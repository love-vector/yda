package ai.yda.framework.rag.retriever.filesystem.util;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public final class ContentUtil {

    public String extractContent(Document document) {
        StringBuilder sb = new StringBuilder();
        String textContent = document.body().text();
        sb.append("Text Content: ").append(textContent).append("\n");
        document.select("h1, h2, h3, h4, h5, h6").forEach(heading -> sb.append("Heading: ").append(heading.text()).append("\n"));
        document.select("a[href]").forEach(link -> sb.append("Link: ").append(link.attr("abs:href")).append(" Text: ").append(link.text()).append("\n"));
        document.select("p").forEach(paragraph -> sb.append("Paragraph: ").append(paragraph.text()).append("\n"));
        return sb.toString();
    }

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