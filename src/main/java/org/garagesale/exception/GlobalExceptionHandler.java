package org.garagesale.exception;

import org.garagesale.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Optional;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> methodArgumentNotValidException(MethodArgumentNotValidException e) {

        Optional<ObjectError> objectError = e.getBindingResult().getAllErrors().stream().findFirst();

        String details = "Unknown error";

        if (objectError.isPresent()) {
            ObjectError err = objectError.get();
            String fieldName = ((FieldError) err).getField();
            String message = err.getDefaultMessage();
            details = fieldName + " : " + message;
        }

        Error error = new Error(
                "Not valid request",
                details
        );

        return new ResponseEntity<>(ApiResponse.withError(error), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> resourceNotFoundException(ResourceNotFoundException e) {
        String message = e.getMessage();
        return new ResponseEntity<>(ApiResponse.withError(message), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<?>> apiException(ApiException e) {
        String message = e.getMessage();
        return new ResponseEntity<>(ApiResponse.withError(message), HttpStatus.NOT_FOUND);
    }
}
