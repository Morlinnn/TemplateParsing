package org.morlinnn.exception;

public class UnknownException extends RuntimeException {
    public UnknownException() {
        super();
    }

    public UnknownException(String message) {
        super(message);
    }
}
