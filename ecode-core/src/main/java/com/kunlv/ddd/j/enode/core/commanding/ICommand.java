package com.kunlv.ddd.j.enode.core.commanding;


import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessage;

public interface ICommand extends IMessage {
    /**
     * Represents the associated aggregate root string id.
     */
    String getAggregateRootId();

    long getStartDeliverTime();

    void setStartDeliverTime(long startDeliverTime);
}
