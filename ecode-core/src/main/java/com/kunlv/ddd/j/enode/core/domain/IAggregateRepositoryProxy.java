package com.kunlv.ddd.j.enode.core.domain;

import com.kunlv.ddd.j.enode.core.infrastructure.IObjectProxy;

public interface IAggregateRepositoryProxy extends IObjectProxy {
    IAggregateRoot get(String aggregateRootId);
}
