package com.kunlv.ddd.j.enode.core.commanding;

public class CommandExecuteTimeoutException extends RuntimeException {

    public CommandExecuteTimeoutException() {
        super();
    }

    public CommandExecuteTimeoutException(String message) {
        super(message);
    }

    public CommandExecuteTimeoutException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
