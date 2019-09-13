package com.kunlv.ddd.j.enode.core.domain;

public interface IAggregateRepository<TAggregateRoot extends IAggregateRoot> {
    TAggregateRoot get(String aggregateRootId);
}
