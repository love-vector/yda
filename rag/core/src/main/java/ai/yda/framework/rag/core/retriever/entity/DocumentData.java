package ai.yda.framework.rag.core.retriever.entity;

import lombok.Getter;

import java.util.Map;

@Getter
public class DocumentData {
    private final String content;

    private final Map<String, Object> metadata;

    public DocumentData(String content, Map<String, Object> metadata) {
        this.content = content;
        this.metadata = metadata;
    }
}
