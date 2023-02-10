package com.ade.chat.exception;

public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException(String msg) {
        super(msg);
    }
}
