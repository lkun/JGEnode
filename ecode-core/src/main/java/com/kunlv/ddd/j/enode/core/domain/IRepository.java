package com.kunlv.ddd.j.enode.core.domain;

public interface IRepository {
    <T extends IAggregateRoot> T get(Class<T> aggregateRootType, Object aggregateRootId);
}
