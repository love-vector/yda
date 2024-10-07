package ai.yda.framework.rag.retriever.website.exception;

/**
 * Thrown to indicate that website processing operation has failed.
 *
 * @author Bogdan Synenko
 * @since 0.1.0
 */
public class WebsiteReadException extends RuntimeException {

    /**
     * Constructs a new {@link  WebsiteReadException} instance with the specified cause.
     * This constructor initializes the exception with a predefined message "Failed to process site".
     *
     * @param cause the cause of the exception, which can be retrieved later using {@link Throwable#getCause()}.
     */
    public WebsiteReadException(final Throwable cause) {
        super("Failed to process site", cause);
    }
}
