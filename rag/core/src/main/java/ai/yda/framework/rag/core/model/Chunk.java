package ai.yda.framework.rag.core.model;

public class Chunk {
    private final String text;
    private final int index;
    private final String documentId;

    public Chunk(String text, int index, String documentId) {
        this.text = text;
        this.index = index;
        this.documentId = documentId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getText() {
        return text;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "Chunk{" +
                "text='" + text + '\'' +
                ", index=" + index +
                ", documentId='" + documentId + '\'' +
                '}';
    }
}