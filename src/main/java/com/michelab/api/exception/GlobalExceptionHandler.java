package com.michelab.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, String>> handleException(HandlerMethodValidationException ex) {
        Map<String, String> body = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getAllErrors().forEach(error -> {
            String name = (error instanceof FieldError fe && fe.getField() != null)
                ? fe.getField()
                : error.getDefaultMessage();
            fieldErrors.put(name, error.getDefaultMessage());
        });

        body.put("status", "ERROR");
        body.put("message", "Validation failed");
        body.put("errors", fieldErrors.toString());
        body.put("timestamp", java.time.LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
