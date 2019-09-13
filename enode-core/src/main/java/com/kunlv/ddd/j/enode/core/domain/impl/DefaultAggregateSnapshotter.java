package com.kunlv.ddd.j.enode.core.domain.impl;

import com.kunlv.ddd.j.enode.common.io.Task;
import com.kunlv.ddd.j.enode.core.domain.IAggregateRepositoryProvider;
import com.kunlv.ddd.j.enode.core.domain.IAggregateRepositoryProxy;
import com.kunlv.ddd.j.enode.core.domain.IAggregateRoot;
import com.kunlv.ddd.j.enode.core.domain.IAggregateSnapshotter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;

/**
 * @author lvk618@gmail.com
 */
public class DefaultAggregateSnapshotter implements IAggregateSnapshotter {
    @Autowired
    private IAggregateRepositoryProvider aggregateRepositoryProvider;

    public DefaultAggregateSnapshotter setAggregateRepositoryProvider(IAggregateRepositoryProvider aggregateRepositoryProvider) {
        this.aggregateRepositoryProvider = aggregateRepositoryProvider;
        return this;
    }

    @Override
    public CompletableFuture<IAggregateRoot> restoreFromSnapshotAsync(Class aggregateRootType, String aggregateRootId) {
        IAggregateRepositoryProxy aggregateRepository = aggregateRepositoryProvider.getRepository(aggregateRootType);
        if (aggregateRepository == null) {
            return Task.completedFuture(null);
        }
        return aggregateRepository.getAsync(aggregateRootId);
    }
}
