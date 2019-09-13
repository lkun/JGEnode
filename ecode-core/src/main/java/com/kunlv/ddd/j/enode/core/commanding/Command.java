package com.kunlv.ddd.j.enode.core.commanding;

import com.kunlv.ddd.j.enode.core.infrastructure.messaging.Message;

/**
 * @param <TAggregateRootId>
 */
public class Command<TAggregateRootId> extends Message implements ICommand {
    public TAggregateRootId aggregateRootId;
    private long startDeliverTime;

    public Command() {
        super();
    }

    public Command(TAggregateRootId aggregateRootId) {
        super();
        if (aggregateRootId == null) {
            throw new NullPointerException("aggregateRootId");
        }
        this.aggregateRootId = aggregateRootId;
    }

    @Override
    public String getAggregateRootId() {
        if (this.aggregateRootId != null)
            return this.aggregateRootId.toString();
        else
            return null;
    }

    @Override
    public long getStartDeliverTime() {
        return startDeliverTime;
    }

    @Override
    public void setStartDeliverTime(long startDeliverTime) {
        this.startDeliverTime = startDeliverTime;
    }

    @Override
    public String getRoutingKey() {
        return getAggregateRootId();
    }
}
