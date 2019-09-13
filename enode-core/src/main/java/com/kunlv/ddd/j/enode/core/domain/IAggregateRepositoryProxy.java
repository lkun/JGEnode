package com.kunlv.ddd.j.enode.core.domain;

import com.kunlv.ddd.j.enode.core.infrastructure.IObjectProxy;

import java.util.concurrent.CompletableFuture;

public interface IAggregateRepositoryProxy extends IObjectProxy {
    CompletableFuture<IAggregateRoot> getAsync(String aggregateRootId);
}
