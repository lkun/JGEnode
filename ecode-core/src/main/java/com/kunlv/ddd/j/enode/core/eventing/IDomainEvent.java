package com.kunlv.ddd.j.enode.core.eventing;

import com.kunlv.ddd.j.enode.core.infrastructure.messaging.ISequenceMessage;

public interface IDomainEvent<TAggregateRootId> extends ISequenceMessage {
    TAggregateRootId aggregateRootId();

    void setAggregateRootId(TAggregateRootId aggregateRootId);
}
