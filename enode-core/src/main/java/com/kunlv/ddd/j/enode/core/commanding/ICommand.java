package com.kunlv.ddd.j.enode.core.commanding;

import com.kunlv.ddd.j.enode.core.messaging.IMessage;

/**
 * Represents a command.
 */
public interface ICommand extends IMessage {
    /**
     * Represents the associated aggregate root string id.
     */
    String getAggregateRootId();
}
