package com.kunlv.ddd.j.enode.core.domain.impl;

import com.kunlv.ddd.j.enode.core.domain.IAggregateRepository;
import com.kunlv.ddd.j.enode.core.domain.IAggregateRepositoryProxy;
import com.kunlv.ddd.j.enode.core.domain.IAggregateRoot;

public class AggregateRepositoryProxy<TAggregateRoot extends IAggregateRoot> implements IAggregateRepositoryProxy {
    private final IAggregateRepository<TAggregateRoot> _aggregateRepository;

    public AggregateRepositoryProxy(IAggregateRepository<TAggregateRoot> aggregateRepository) {
        _aggregateRepository = aggregateRepository;
    }

    public Object getInnerObject() {
        return _aggregateRepository;
    }

    public IAggregateRoot get(String aggregateRootId) {
        return _aggregateRepository.get(aggregateRootId);
    }
}
