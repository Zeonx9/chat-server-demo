package com.ade.chat.exception;

public class WrongAuthHeaderException extends RuntimeException {
    public WrongAuthHeaderException(String s) {
        super(s);
    }
}
