package com.poseidoncapital.exceptions;

public class ValueToLargeException extends RuntimeException {
    public ValueToLargeException(String message) {
        super(message);
    }
}
