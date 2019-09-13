package com.kunlv.ddd.j.enode.core.commanding;

import com.kunlv.ddd.j.enode.core.domain.IAggregateRoot;

public interface ICommandContext {
    void add(IAggregateRoot aggregateRoot);

    <T extends IAggregateRoot> T get(Class<T> aggregateRootType, Object id);

    <T extends IAggregateRoot> T get(Class<T> aggregateRootType, Object id, boolean firstFromCache);

    void setResult(String result);

    String getResult();
}
