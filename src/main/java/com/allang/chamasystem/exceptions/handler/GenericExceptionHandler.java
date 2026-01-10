package com.allang.chamasystem.exceptions.handler;

import com.allang.chamasystem.exceptions.GenericExceptions;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GenericExceptionHandler {

    @ExceptionHandler(GenericExceptions.class)
    public Mono<@NonNull ResponseEntity<@NonNull String>> handleGenericException(GenericExceptions ex) {
        return Mono.just(ResponseEntity
                .badRequest()
                .body(ex.getMessage()));
    }
}
