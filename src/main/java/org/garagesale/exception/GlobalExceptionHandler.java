package org.garagesale.exception;

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
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) {

        Optional<ObjectError> objectError = e.getBindingResult().getAllErrors().stream().findFirst();

        String details = "Unknown error";

        if (objectError.isPresent()) {
            ObjectError err = objectError.get();
            String fieldName = ((FieldError) err).getField();
            String message = err.getDefaultMessage();
            details = fieldName + " : " + message;
        }

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Not valid request",
                details
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
