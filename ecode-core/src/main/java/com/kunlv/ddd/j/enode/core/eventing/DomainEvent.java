package com.kunlv.ddd.j.enode.core.eventing;

import com.kunlv.ddd.j.enode.core.infrastructure.messaging.SequenceMessage;

public abstract class DomainEvent<TAggregateRootId> extends SequenceMessage<TAggregateRootId>
        implements IDomainEvent<TAggregateRootId> {

}
