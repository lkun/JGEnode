package com.kunlv.ddd.j.enode.core.domain;

import com.kunlv.ddd.j.enode.common.function.Action2;
import com.kunlv.ddd.j.enode.core.eventing.IDomainEvent;

public interface IAggregateRootInternalHandlerProvider {
    Action2<IAggregateRoot,IDomainEvent> getInternalEventHandler(Class<? extends IAggregateRoot> aggregateRootType, Class<? extends IDomainEvent> anEventType);
}
