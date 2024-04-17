package org.vector.assistant.exception.not.found;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Intention does not exist")
public class IntentionDoesNotExistException extends RuntimeException {}
