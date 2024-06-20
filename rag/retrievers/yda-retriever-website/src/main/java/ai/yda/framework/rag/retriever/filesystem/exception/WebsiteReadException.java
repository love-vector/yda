package ai.yda.framework.rag.retriever.filesystem.exception;

public class WebsiteReadException extends RuntimeException {

    public WebsiteReadException(final Throwable cause) {
        super("Failed to download site", cause);
    }
}
