package com.sandu.trackr.exception;

public class GoogleLoginException extends RuntimeException {
    public GoogleLoginException(Exception message) {
        super(message);
    }

}
