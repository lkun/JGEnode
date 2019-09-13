package com.kunlv.ddd.j.enode.queue.rocketmq.client.ons;

public class SpasException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public SpasException(String message, Throwable t) {
        super(message, t);
    }


    public SpasException(String message) {
        super(message);
    }
}