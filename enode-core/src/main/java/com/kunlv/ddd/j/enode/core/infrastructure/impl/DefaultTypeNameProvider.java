package com.kunlv.ddd.j.enode.core.infrastructure.impl;

import com.kunlv.ddd.j.enode.common.exception.ENodeRuntimeException;
import com.kunlv.ddd.j.enode.core.infrastructure.ITypeNameProvider;

/**
 * @author lvk618@gmail.com
 */
public class DefaultTypeNameProvider implements ITypeNameProvider {
    @Override
    public String getTypeName(Class type) {
        return type.getName();
    }

    @Override
    public Class getType(String typeName) {
        try {
            return Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            throw new ENodeRuntimeException("ClassNotFound", e);
        }
    }
}
