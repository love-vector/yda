package org.vector.assistant.exception.conflict;

public class AssistantAlreadyExistsException extends RuntimeException {

    public AssistantAlreadyExistsException() {
        super("Assistant already exists");
    }
}
