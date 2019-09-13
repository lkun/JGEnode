package com.kunlv.ddd.j.enode.core.domain.impl;

import com.kunlv.ddd.j.enode.core.domain.IAggregateRoot;
import com.kunlv.ddd.j.enode.core.domain.IAggregateRootFactory;

public class DefaultAggregateRootFactory implements IAggregateRootFactory {

    @Override
    public <T extends IAggregateRoot> T createAggregateRoot(Class<T> aggregateRootType) {
        try {
            return aggregateRootType.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
