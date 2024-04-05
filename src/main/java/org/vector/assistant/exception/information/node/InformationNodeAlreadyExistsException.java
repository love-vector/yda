package org.vector.assistant.exception.information.node;

public class InformationNodeAlreadyExistsException extends RuntimeException {

    public InformationNodeAlreadyExistsException() {
        super("Information Node already exists");
    }
}
