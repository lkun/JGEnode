package com.kunlv.ddd.j.enode.core.domain.impl;

import com.kunlv.ddd.j.enode.core.domain.IAggregateRepository;
import com.kunlv.ddd.j.enode.core.domain.IAggregateRepositoryProxy;
import com.kunlv.ddd.j.enode.core.domain.IAggregateRoot;

import java.util.concurrent.CompletableFuture;

/**
 * @author lvk618@gmail.com
 */
public class AggregateRepositoryProxy<TAggregateRoot extends IAggregateRoot> implements IAggregateRepositoryProxy {
    private final IAggregateRepository<TAggregateRoot> aggregateRepository;

    public AggregateRepositoryProxy(IAggregateRepository<TAggregateRoot> aggregateRepository) {
        this.aggregateRepository = aggregateRepository;
    }

    @Override
    public Object getInnerObject() {
        return aggregateRepository;
    }

    @Override
    public CompletableFuture<IAggregateRoot> getAsync(String aggregateRootId) {
        return (CompletableFuture<IAggregateRoot>) aggregateRepository.getAsync(aggregateRootId);
    }
}
