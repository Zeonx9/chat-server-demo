package com.ade.chat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<AppError> catchNameAlreadyTakenException(NameAlreadyTakenException e) {
        System.err.println(e.getMessage() + " это я обработал");
        return new ResponseEntity<>(
                new AppError(e.getMessage(), HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchAuthenticationException(AuthenticationException e) {
        System.err.println(e.getMessage() + " это я обработал");
        return new ResponseEntity<>(
                new AppError(e.getMessage(), HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST
        );
    }
}
