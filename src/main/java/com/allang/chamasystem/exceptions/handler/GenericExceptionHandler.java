package com.allang.chamasystem.exceptions.handler;

import com.allang.chamasystem.dto.ResponseDto;
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
    public Mono<@NonNull ResponseEntity<@NonNull ResponseDto>> handleGenericException(GenericExceptions ex) {
        return Mono.just(ResponseEntity
                .status(400)
                .body(new ResponseDto(ex.getMessage(), null, false, 400)));
    }
}
