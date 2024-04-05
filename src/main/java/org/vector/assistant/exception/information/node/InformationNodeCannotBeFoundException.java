package org.vector.assistant.exception.information.node;

public class InformationNodeCannotBeFoundException extends RuntimeException {

    public InformationNodeCannotBeFoundException() {
        super("Can't find information node");
    }
}
