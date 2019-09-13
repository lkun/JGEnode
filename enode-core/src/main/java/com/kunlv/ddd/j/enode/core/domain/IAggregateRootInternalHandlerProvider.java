package com.kunlv.ddd.j.enode.core.domain;

import com.kunlv.ddd.j.enode.common.function.Action2;
import com.kunlv.ddd.j.enode.core.eventing.IDomainEvent;

/**
 * Defines a provider interface to provide the aggregate root internal handler.
 */
public interface IAggregateRootInternalHandlerProvider {
    /**
     * Get the internal event handler within the aggregate.
     *
     * @param aggregateRootType
     * @param anEventType
     * @return
     */
    Action2<IAggregateRoot, IDomainEvent> getInternalEventHandler(Class<? extends IAggregateRoot> aggregateRootType, Class<? extends IDomainEvent> anEventType);
}
