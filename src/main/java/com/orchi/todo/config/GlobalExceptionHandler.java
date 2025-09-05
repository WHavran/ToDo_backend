package com.orchi.todo.config;

import com.orchi.todo.model.messages.ErrorResponse;
import com.orchi.todo.model.messages.ErrorResponseValidation;
import com.orchi.todo.model.messages.ErrorValidationField;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseValidation> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<ErrorValidationField> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new ErrorValidationField(err.getField(), err.getDefaultMessage()))
                .sorted(Comparator.comparing(ErrorValidationField::field)
                        .thenComparing(ErrorValidationField::message))
                .toList();

        ErrorResponseValidation response = new ErrorResponseValidation(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                fieldErrors
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseException(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();
        String message;
        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException &&
                cause.getMessage().contains("Status")) {
            message = "Invalid status. Allowed values: CREATED, IN_PROCESS, FINISHED, FAILED.";
        } else {
            message = "Invalid input: " + cause.getMessage();
        }
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                message
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotExistEntity (EntityNotFoundException ex){
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Entity not found"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleBasicExs(MethodArgumentTypeMismatchException ex){
        String message = "Invalid parameter: " + ex.getValue();
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                message);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseValidation> handleConstraintViolations(ConstraintViolationException ex) {
        List<ErrorValidationField> fieldErrors = ex.getConstraintViolations().stream()
                .map(violation -> new ErrorValidationField(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                ))
                .sorted(Comparator.comparing(ErrorValidationField::field)
                        .thenComparing(ErrorValidationField::message))
                .toList();

        ErrorResponseValidation response = new ErrorResponseValidation(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                fieldErrors
        );
        return ResponseEntity.badRequest().body(response);
    }

}
