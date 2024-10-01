package ai.yda.framework.rag.core.model;

public class Chunk {
    private final String text;
    private final int index;
    private final String url;

    public Chunk(String text, int index, String url) {
        this.text = text;
        this.index = index;
        this.url = url;
    }

    public String getUrl() {
        return url;
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
                ", url='" + url + '\'' +
                '}';
    }
}