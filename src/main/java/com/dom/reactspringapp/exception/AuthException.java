package com.dom.reactspringapp.exception;

public class AuthException extends ApiException {


    public AuthException(String message, String errorCode) {
        super(message, errorCode);
    }
}
