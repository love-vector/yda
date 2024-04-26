package org.vector.yda.exception.not.found;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Intention not found")
public class IntentionNotFoundException extends RuntimeException {}
