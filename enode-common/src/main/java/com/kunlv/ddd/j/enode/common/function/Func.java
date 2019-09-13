package com.kunlv.ddd.j.enode.common.function;

public interface Func<TResult> {
    TResult apply() throws Exception;
}
