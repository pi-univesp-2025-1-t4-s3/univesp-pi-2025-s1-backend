package com.univesp.pi.pji310.s3.t4.pi_2025_s1.config.handler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class })
    public ResponseEntity<Object> handleValidationErrors(Exception ex) {
        Map<String, String> erros = new HashMap<>();

        if (ex instanceof MethodArgumentNotValidException e) {
            e.getBindingResult().getFieldErrors()
                    .forEach(error -> erros.put(error.getField(), error.getDefaultMessage()));
        } else if (ex instanceof BindException e) {
            e.getBindingResult().getFieldErrors()
                    .forEach(error -> erros.put(error.getField(), error.getDefaultMessage()));
        }

        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolations(ConstraintViolationException ex) {
        Map<String, String> erros = new HashMap<>();
        ex.getConstraintViolations().forEach(v -> erros.put(v.getPropertyPath().toString(), v.getMessage()));
        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
    }
}
