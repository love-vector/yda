package org.vector.yda.web.advice;

import org.springframework.ai.openai.api.common.OpenAiApiException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler({DuplicateKeyException.class, DataIntegrityViolationException.class})
    protected ResponseEntity<Object> handleConflict(final Exception exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler({OpenAiApiException.class, InvalidDataAccessApiUsageException.class})
    protected ResponseEntity<Object> handleInternalServerError(final Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }
}
