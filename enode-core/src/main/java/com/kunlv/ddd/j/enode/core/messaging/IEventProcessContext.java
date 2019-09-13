package com.kunlv.ddd.j.enode.core.messaging;

/**
 * Represents the event processing context.
 */
public interface IEventProcessContext {
    /**
     * Notify the event has been processed.
     */
    void notifyEventProcessed();
}
