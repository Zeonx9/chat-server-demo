package com.ade.chat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static ResponseEntity<AppError> handleTheException(Exception e, HttpStatus status) {
        System.err.println(e.getMessage());
        return new ResponseEntity<>(
                new AppError(e.getMessage(), status.value()),
                status
        );
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchNameAlreadyTakenException(NameAlreadyTakenException e) {
        return handleTheException(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchAuthenticationException(AuthenticationException e) {
        return handleTheException(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchUserNotFoundException(UserNotFoundException e) {
       return handleTheException(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchChatNotFoundException(ChatNotFoundException e) {
        return handleTheException(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchChatIllegalMemberCount(IllegalMemberCount e) {
        return handleTheException(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchNotAMemberException(NotAMemberException e) {
        return handleTheException(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchCompanyNotFoundException(CompanyNotFoundException e) {
        return handleTheException(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchAbsentGroupInfo(AbsentGroupInfoException e) {
        return handleTheException(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchUploadFailed(UploadFailedException e) {
        return handleTheException(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
