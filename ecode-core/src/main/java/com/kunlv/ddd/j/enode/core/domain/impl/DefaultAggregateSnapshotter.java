package com.kunlv.ddd.j.enode.core.domain.impl;

import  com.kunlv.ddd.j.enode.core.domain.IAggregateRepositoryProvider;
import  com.kunlv.ddd.j.enode.core.domain.IAggregateRepositoryProxy;
import  com.kunlv.ddd.j.enode.core.domain.IAggregateRoot;
import  com.kunlv.ddd.j.enode.core.domain.IAggregateSnapshotter;

import javax.inject.Inject;

public class DefaultAggregateSnapshotter implements IAggregateSnapshotter {
    private final IAggregateRepositoryProvider _aggregateRepositoryProvider;

    @Inject
    public DefaultAggregateSnapshotter(IAggregateRepositoryProvider aggregateRepositoryProvider) {
        _aggregateRepositoryProvider = aggregateRepositoryProvider;
    }

    public IAggregateRoot restoreFromSnapshot(Class aggregateRootType, String aggregateRootId) {
        IAggregateRepositoryProxy aggregateRepository = _aggregateRepositoryProvider.getRepository(aggregateRootType);
        if (aggregateRepository != null) {
            return aggregateRepository.get(aggregateRootId);
        }
        return null;
    }
}
