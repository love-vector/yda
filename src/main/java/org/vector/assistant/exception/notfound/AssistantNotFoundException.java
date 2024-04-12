package org.vector.assistant.exception.notfound;

public class AssistantNotFoundException extends RuntimeException {

    public AssistantNotFoundException() {
        super("Assistant does not exist");
    }

    public AssistantNotFoundException(final String message) {
        super(message);
    }
}
