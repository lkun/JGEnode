package com.kunlv.ddd.j.enode.common.exception;

public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException() {
        super();
    }

    public InvalidOperationException(String msg) {
        super(msg);
    }

    public InvalidOperationException(Throwable cause) {
        super(cause);
    }

    public InvalidOperationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
