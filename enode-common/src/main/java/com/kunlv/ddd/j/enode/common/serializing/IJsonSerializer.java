package com.kunlv.ddd.j.enode.common.serializing;

public interface IJsonSerializer {
    String serialize(Object obj);

    <T> T deserialize(String aSerialization, final Class<T> aType);
}
