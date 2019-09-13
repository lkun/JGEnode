package com.kunlv.ddd.j.enode.core.domain.impl;

import com.kunlv.ddd.j.enode.core.domain.IAggregateRoot;
import com.kunlv.ddd.j.enode.core.domain.IAggregateSnapshotter;
import com.kunlv.ddd.j.enode.core.domain.IAggregateStorage;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;

/**
 * @author lvk618@gmail.com
 */
public class SnapshotOnlyAggregateStorage implements IAggregateStorage {
    @Autowired
    private IAggregateSnapshotter aggregateSnapshotter;

    public SnapshotOnlyAggregateStorage setAggregateSnapshotter(IAggregateSnapshotter aggregateSnapshotter) {
        this.aggregateSnapshotter = aggregateSnapshotter;
        return this;
    }

    @Override
    public <T extends IAggregateRoot> CompletableFuture<T> getAsync(Class<T> aggregateRootType, String aggregateRootId) {
        if (aggregateRootType == null) {
            throw new NullPointerException("aggregateRootType");
        }
        if (aggregateRootId == null) {
            throw new NullPointerException("aggregateRootId");
        }
        CompletableFuture<T> future = aggregateSnapshotter.restoreFromSnapshotAsync(aggregateRootType, aggregateRootId);
        return future.thenApply(aggregateRoot -> {
            if (aggregateRoot != null && (aggregateRoot.getClass() != aggregateRootType || !aggregateRoot.getUniqueId().equals(aggregateRootId))) {
                throw new RuntimeException(String.format("AggregateRoot recovery from snapshot is invalid as the aggregateRootType or aggregateRootId is not matched. Snapshot: [aggregateRootType:%s,aggregateRootId:%s], expected: [aggregateRootType:%s,aggregateRootId:%s]",
                        aggregateRoot.getClass(),
                        aggregateRoot.getUniqueId(),
                        aggregateRootType,
                        aggregateRootId));
            }
            return aggregateRoot;
        });
    }
}
