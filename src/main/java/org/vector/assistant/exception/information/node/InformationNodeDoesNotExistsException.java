package org.vector.assistant.exception.information.node;

public class InformationNodeDoesNotExistsException extends RuntimeException {

    public InformationNodeDoesNotExistsException() {
        super("Information node does not exist");
    }
}
