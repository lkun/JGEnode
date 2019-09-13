package com.kunlv.ddd.j.enode.core.infrastructure;

public class WrappedRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -8951926710590746149L;

    /**
     * checked exception
     */
    private Exception exception;

    public WrappedRuntimeException(Exception e) {
        super(e.getMessage());
        exception = e instanceof WrappedRuntimeException ? ((WrappedRuntimeException) e).getException() : e;
    }

    public WrappedRuntimeException(String msg, Exception e) {
        super(msg);
        exception = e instanceof WrappedRuntimeException ? ((WrappedRuntimeException) e).getException() : e;
    }

    public Exception getException() {
        return exception;
    }
}
