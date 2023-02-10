package com.ade.chat.exception;

public class NotAMemberException extends RuntimeException {
    public NotAMemberException(String msg) {
        super(msg);
    }
}
