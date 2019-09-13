package com.kunlv.ddd.j.enode.core.infrastructure;

import  com.kunlv.ddd.j.enode.common.function.Action;

public interface ILockService {
    void addLockKey(String lockKey);

    void executeInLock(String lockKey, Action action);
}
