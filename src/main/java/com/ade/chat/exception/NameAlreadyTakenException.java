package com.ade.chat.exception;

public class NameAlreadyTakenException extends RuntimeException{
    public NameAlreadyTakenException(String message) {
        super(message);
    }
}
