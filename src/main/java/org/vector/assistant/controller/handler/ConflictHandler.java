package org.vector.assistant.controller.handler;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;

import org.vector.assistant.exception.conflict.AssistantAlreadyExistsException;
import org.vector.assistant.exception.conflict.UserAlreadyExistsException;

@Slf4j
@RestControllerAdvice
public class ConflictHandler extends ResponseEntityExceptionHandler {

    private static final HttpStatus code = HttpStatus.CONFLICT;

    @ExceptionHandler({UserAlreadyExistsException.class, AssistantAlreadyExistsException.class})
    protected Mono<ResponseEntity<Object>> handleInvalidTokenException(
            final RuntimeException exception, final ServerWebExchange exchange) {
        log.error(exception.getMessage());
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(), code, exchange);
    }
}
