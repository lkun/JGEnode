package com.kunlv.ddd.j.enode.core.infrastructure;

import java.util.function.Consumer;

public interface When<ExpectType extends Throwable, OrigType extends Throwable> {
    WrappedExceptionParser<OrigType> then(Consumer<ExpectType> consumer);
}
