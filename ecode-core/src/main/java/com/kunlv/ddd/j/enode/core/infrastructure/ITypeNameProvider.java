package com.kunlv.ddd.j.enode.core.infrastructure;

public interface ITypeNameProvider {
    String getTypeName(Class type);

    Class getType(String typeName);
}
