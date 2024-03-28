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

import org.vector.assistant.exception.UserAlreadyExistsException;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    protected Mono<ResponseEntity<Object>> handleInvalidTokenException(
            final RuntimeException exception, final ServerWebExchange exchange) {
        log.error(exception.getMessage());
        return handleExceptionInternal(
                exception, exception.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT, exchange);
    }
}
