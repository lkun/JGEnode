package com.kunlv.ddd.j.enode.core.domain.impl;

import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.common.io.Task;
import com.kunlv.ddd.j.enode.core.domain.IAggregateRoot;
import com.kunlv.ddd.j.enode.core.domain.IAggregateRootFactory;
import com.kunlv.ddd.j.enode.core.domain.IAggregateSnapshotter;
import com.kunlv.ddd.j.enode.core.domain.IAggregateStorage;
import com.kunlv.ddd.j.enode.core.eventing.DomainEventStream;
import com.kunlv.ddd.j.enode.core.eventing.IEventStore;
import com.kunlv.ddd.j.enode.core.infrastructure.ITypeNameProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author lvk618@gmail.com
 */
public class EventSourcingAggregateStorage implements IAggregateStorage {
    private static final int MINVERSION = 1;
    private static final int MAXVERSION = Integer.MAX_VALUE;
    @Autowired
    private IAggregateRootFactory aggregateRootFactory;
    @Autowired
    private IEventStore eventStore;
    @Autowired
    private IAggregateSnapshotter aggregateSnapshotter;
    @Autowired
    private ITypeNameProvider typeNameProvider;

    public EventSourcingAggregateStorage setAggregateRootFactory(IAggregateRootFactory aggregateRootFactory) {
        this.aggregateRootFactory = aggregateRootFactory;
        return this;
    }

    public EventSourcingAggregateStorage setEventStore(IEventStore eventStore) {
        this.eventStore = eventStore;
        return this;
    }

    public EventSourcingAggregateStorage setAggregateSnapshotter(IAggregateSnapshotter aggregateSnapshotter) {
        this.aggregateSnapshotter = aggregateSnapshotter;
        return this;
    }

    public EventSourcingAggregateStorage setTypeNameProvider(ITypeNameProvider typeNameProvider) {
        this.typeNameProvider = typeNameProvider;
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
        CompletableFuture<T> aggregateRootFuture = tryGetFromSnapshot(aggregateRootId, aggregateRootType);
        return aggregateRootFuture.thenCompose(aggregateRoot -> {
            if (aggregateRoot != null) {
                return CompletableFuture.completedFuture(aggregateRoot);
            }
            String aggregateRootTypeName = typeNameProvider.getTypeName(aggregateRootType);
            CompletableFuture<AsyncTaskResult<List<DomainEventStream>>> eventStreamsFuture = eventStore.queryAggregateEventsAsync(aggregateRootId, aggregateRootTypeName, MINVERSION, MAXVERSION);
            return eventStreamsFuture.thenApply(eventStreams -> {
                List<DomainEventStream> domainEventStreams = eventStreams.getData();
                T reAggregateRoot = rebuildAggregateRoot(aggregateRootType, domainEventStreams);
                return reAggregateRoot;
            });
        });
    }

    private <T extends IAggregateRoot> CompletableFuture<T> tryGetFromSnapshot(String aggregateRootId, Class<T> aggregateRootType) {
        CompletableFuture<T> aggregateRootFuture = aggregateSnapshotter.restoreFromSnapshotAsync(aggregateRootType, aggregateRootId);
        CompletableFuture<T> ret = aggregateRootFuture.thenCompose((aggregateRoot) -> {
            if (aggregateRoot == null) {
                return Task.completedFuture(null);
            }
            if (aggregateRoot.getClass() != aggregateRootType || !aggregateRoot.getUniqueId().equals(aggregateRootId)) {
                throw new RuntimeException(String.format("AggregateRoot recovery from snapshot is invalid as the aggregateRootType or aggregateRootId is not matched. Snapshot: [aggregateRootType:%s,aggregateRootId:%s], expected: [aggregateRootType:%s,aggregateRootId:%s]",
                        aggregateRoot.getClass(),
                        aggregateRoot.getUniqueId(),
                        aggregateRootType,
                        aggregateRootId));
            }
            String aggregateRootTypeName = typeNameProvider.getTypeName(aggregateRootType);
            CompletableFuture<AsyncTaskResult<List<DomainEventStream>>> eventStreamsFuture = eventStore.queryAggregateEventsAsync(aggregateRootId, aggregateRootTypeName, aggregateRoot.getVersion() + 1, MAXVERSION);
            return eventStreamsFuture.thenApply(eventStreams -> {
                List<DomainEventStream> eventStreamsAfterSnapshot = eventStreams.getData();
                aggregateRoot.replayEvents(eventStreamsAfterSnapshot);
                return aggregateRoot;
            });
        });
        return ret;
    }

    private <T extends IAggregateRoot> T rebuildAggregateRoot(Class<T> aggregateRootType, List<DomainEventStream> eventStreams) {
        if (eventStreams == null || eventStreams.isEmpty()) {
            return null;
        }
        T aggregateRoot = aggregateRootFactory.createAggregateRoot(aggregateRootType);
        aggregateRoot.replayEvents(eventStreams);
        return aggregateRoot;
    }
}
