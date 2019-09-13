package com.kunlv.ddd.j.enode.core.domain.impl;

import com.kunlv.ddd.j.enode.core.domain.IAggregateRoot;
import com.kunlv.ddd.j.enode.core.domain.IMemoryCache;
import com.kunlv.ddd.j.enode.core.domain.IRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;

/**
 * @author lvk618@gmail.com
 */
public class DefaultRepository implements IRepository {
    @Autowired
    private IMemoryCache memoryCache;

    public DefaultRepository setMemoryCache(IMemoryCache memoryCache) {
        this.memoryCache = memoryCache;
        return this;
    }

    @Override
    public <T extends IAggregateRoot> CompletableFuture<T> getAsync(Class<T> aggregateRootType, Object aggregateRootId) {
        if (aggregateRootType == null) {
            throw new NullPointerException("aggregateRootType");
        }
        if (aggregateRootId == null) {
            throw new NullPointerException("aggregateRootId");
        }
        CompletableFuture<T> future = memoryCache.getAsync(aggregateRootId, aggregateRootType);
        return future.thenCompose(aggregateRoot -> {
            if (aggregateRoot == null) {
                return memoryCache.refreshAggregateFromEventStoreAsync(aggregateRootType, aggregateRootId);
            }
            return CompletableFuture.completedFuture(aggregateRoot);
        });
    }

    /**
     * Get an aggregate from memory cache, if not exist, get it from event store.
     *
     * @param aggregateRootId
     * @return
     */
    @Override
    public CompletableFuture<IAggregateRoot> getAsync(Object aggregateRootId) {
        return getAsync(IAggregateRoot.class, aggregateRootId);
    }
}
