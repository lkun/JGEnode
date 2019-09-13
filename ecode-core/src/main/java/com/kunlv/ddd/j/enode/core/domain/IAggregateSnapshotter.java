package com.kunlv.ddd.j.enode.core.domain;

public interface IAggregateSnapshotter {
    <T extends IAggregateRoot> T restoreFromSnapshot(Class<T> aggregateRootType, String aggregateRootId);
}
