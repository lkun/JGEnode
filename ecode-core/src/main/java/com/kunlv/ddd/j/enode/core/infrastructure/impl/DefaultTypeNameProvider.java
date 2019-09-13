package com.kunlv.ddd.j.enode.core.infrastructure.impl;

import  com.kunlv.ddd.j.enode.core.infrastructure.ITypeNameProvider;

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
            throw new RuntimeException(e);
        }
    }
}
