package ai.yda.framework.rag.retriever.website.exception;

public class WebsiteReadException extends RuntimeException {

    public WebsiteReadException(final Throwable cause) {
        super("Failed to process site", cause);
    }
}
