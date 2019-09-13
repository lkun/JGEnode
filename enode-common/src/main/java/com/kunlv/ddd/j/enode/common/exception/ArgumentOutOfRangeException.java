package com.kunlv.ddd.j.enode.common.exception;

public class ArgumentOutOfRangeException extends RuntimeException {
    public ArgumentOutOfRangeException() {
        super();
    }

    public ArgumentOutOfRangeException(String msg) {
        super(msg);
    }

    public ArgumentOutOfRangeException(Throwable cause) {
        super(cause);
    }

    public ArgumentOutOfRangeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
