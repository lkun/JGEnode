package com.kunlv.ddd.j.enode.core.domain.impl;

import com.kunlv.ddd.j.enode.common.exception.ENodeRuntimeException;
import com.kunlv.ddd.j.enode.core.domain.IAggregateRoot;
import com.kunlv.ddd.j.enode.core.domain.IAggregateRootFactory;

/**
 * @author lvk618@gmail.com
 */
public class DefaultAggregateRootFactory implements IAggregateRootFactory {
    @Override
    public <T extends IAggregateRoot> T createAggregateRoot(Class<T> aggregateRootType) {
        try {
            return aggregateRootType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ENodeRuntimeException(e);
        }
    }
}
