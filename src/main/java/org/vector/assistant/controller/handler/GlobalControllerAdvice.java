package org.vector.assistant.controller.handler;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;

import org.vector.assistant.exception.UserAlreadyExistsException;
import org.vector.assistant.exception.information.node.InformationNodeDoesNotExistsException;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InformationNodeDoesNotExistsException.class)
    protected Mono<ResponseEntity<Object>> handleNotFound(
            final RuntimeException exception, final ServerWebExchange exchange) {
        log.error(exception.getMessage());
        return handleExceptionInternal(
                exception, exception.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, exchange);
    }

    @ExceptionHandler({UserAlreadyExistsException.class, DuplicateKeyException.class})
    protected Mono<ResponseEntity<Object>> handleConflict(
            final RuntimeException exception, final ServerWebExchange exchange) {
        log.error(exception.getMessage());
        return handleExceptionInternal(
                exception, exception.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT, exchange);
    }
}
