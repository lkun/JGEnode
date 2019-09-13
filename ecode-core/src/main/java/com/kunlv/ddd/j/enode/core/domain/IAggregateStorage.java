package com.kunlv.ddd.j.enode.core.domain;

public interface IAggregateStorage {
    <T extends IAggregateRoot> T get(Class<T> aggregateRootType, String aggregateRootId);
}
