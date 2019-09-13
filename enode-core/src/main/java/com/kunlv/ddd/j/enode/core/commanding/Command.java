package com.kunlv.ddd.j.enode.core.commanding;

import com.kunlv.ddd.j.enode.core.messaging.Message;

/**
 * @author lvk618@gmail.com
 */
public class Command<TAggregateRootId> extends Message implements ICommand {
    public TAggregateRootId aggregateRootId;

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
        if (aggregateRootId != null) {
            return aggregateRootId.toString();
        }
        return null;
    }

    public void setAggregateRootId(TAggregateRootId aggregateRootId) {
        this.aggregateRootId = aggregateRootId;
    }

}
