package com.kunlv.ddd.j.enode.core.infrastructure;

import java.util.Set;

public interface IAssemblyInitializer {
    void initialize(Set<Class<?>> componentTypes);
}
