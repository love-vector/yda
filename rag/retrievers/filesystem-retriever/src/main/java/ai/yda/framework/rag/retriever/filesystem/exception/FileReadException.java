package ai.yda.framework.rag.retriever.filesystem.exception;

public class FileReadException extends RuntimeException {

    public FileReadException(final Throwable cause) {
        super("Failed to read file", cause);
    }
}
