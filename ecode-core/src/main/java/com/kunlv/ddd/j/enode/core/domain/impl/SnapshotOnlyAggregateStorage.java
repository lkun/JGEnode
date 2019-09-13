package com.kunlv.ddd.j.enode.core.domain.impl;

import  com.kunlv.ddd.j.enode.core.domain.IAggregateRoot;
import  com.kunlv.ddd.j.enode.core.domain.IAggregateSnapshotter;
import  com.kunlv.ddd.j.enode.core.domain.IAggregateStorage;

import javax.inject.Inject;

public class SnapshotOnlyAggregateStorage implements IAggregateStorage {
    private final IAggregateSnapshotter _aggregateSnapshotter;

    @Inject
    public SnapshotOnlyAggregateStorage(IAggregateSnapshotter aggregateSnapshotter) {
        _aggregateSnapshotter = aggregateSnapshotter;
    }

    public <T extends IAggregateRoot> T get(Class<T> aggregateRootType, String aggregateRootId) {
        {
            if (aggregateRootType == null) throw new NullPointerException("aggregateRootType");
            if (aggregateRootId == null) throw new NullPointerException("aggregateRootId");

            T aggregateRoot = _aggregateSnapshotter.restoreFromSnapshot(aggregateRootType, aggregateRootId);

            if (aggregateRoot != null && (aggregateRoot.getClass() != aggregateRootType || aggregateRoot.uniqueId() != aggregateRootId)) {
                throw new RuntimeException(String.format("AggregateRoot recovery from snapshot is invalid as the aggregateRootType or aggregateRootId is not matched. Snapshot: [aggregateRootType:%s,aggregateRootId:%s], expected: [aggregateRootType:%s,aggregateRootId:%s]",
                        aggregateRoot.getClass(),
                        aggregateRoot.uniqueId(),
                        aggregateRootType,
                        aggregateRootId));
            }

            return aggregateRoot;
        }
    }
}
