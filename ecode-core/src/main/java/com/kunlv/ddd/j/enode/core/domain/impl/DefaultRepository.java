package com.kunlv.ddd.j.enode.core.domain.impl;

import  com.kunlv.ddd.j.enode.core.domain.IAggregateRoot;
import  com.kunlv.ddd.j.enode.core.domain.IAggregateStorage;
import  com.kunlv.ddd.j.enode.core.domain.IMemoryCache;
import  com.kunlv.ddd.j.enode.core.domain.IRepository;

import javax.inject.Inject;

public class DefaultRepository implements IRepository {
    private final IMemoryCache _memoryCache;
    private final IAggregateStorage _aggregateRootStorage;

    @Inject
    public DefaultRepository(IMemoryCache memoryCache, IAggregateStorage aggregateRootStorage) {
        _memoryCache = memoryCache;
        _aggregateRootStorage = aggregateRootStorage;
    }

    public <T extends IAggregateRoot> T get(Class<T> aggregateRootType, Object aggregateRootId) {
        if (aggregateRootType == null) {
            throw new NullPointerException("aggregateRootType");
        }
        if (aggregateRootId == null) {
            throw new NullPointerException("aggregateRootId");
        }

        T aggregateRoot = _memoryCache.get(aggregateRootId, aggregateRootType);
        return aggregateRoot == null ? _aggregateRootStorage.get(aggregateRootType, aggregateRootId.toString()) : aggregateRoot;
    }
}
