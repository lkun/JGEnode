package com.kunlv.ddd.j.enode.core.domain;

public interface IAggregateRepositoryProvider {
    IAggregateRepositoryProxy getRepository(Class<? extends IAggregateRoot> aggregateRootType);
}
