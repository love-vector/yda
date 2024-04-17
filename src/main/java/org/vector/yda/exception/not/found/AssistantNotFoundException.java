package org.vector.yda.exception.not.found;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Assistant not found")
public class AssistantNotFoundException extends RuntimeException {}
