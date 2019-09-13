package com.kunlv.ddd.j.enode.core.infrastructure.impl.inmemory;

import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.common.io.AsyncTaskStatus;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessageHandleRecordStore;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.MessageHandleRecord;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.ThreeMessageHandleRecord;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.TwoMessageHandleRecord;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryMessageHandleRecordStore implements IMessageHandleRecordStore {
    private final CompletableFuture<AsyncTaskResult> _successTask = CompletableFuture.completedFuture(AsyncTaskResult.Success);
    private final ConcurrentMap<String, Integer> _dict = new ConcurrentHashMap<>();

    public CompletableFuture<AsyncTaskResult> addRecordAsync(MessageHandleRecord record) {
        _dict.put(record.getMessageId() + record.getHandlerTypeName(), 0);
        return _successTask;
    }

    public CompletableFuture<AsyncTaskResult> addRecordAsync(TwoMessageHandleRecord record) {
        _dict.put(record.getMessageId1() + record.getMessageId2() + record.getHandlerTypeName(), 0);
        return _successTask;
    }

    public CompletableFuture<AsyncTaskResult> addRecordAsync(ThreeMessageHandleRecord record) {
        _dict.put(record.getMessageId1() + record.getMessageId2() + record.getMessageId3() + record.getHandlerTypeName(), 0);
        return _successTask;
    }

    public CompletableFuture<AsyncTaskResult<Boolean>> isRecordExistAsync(String messageId, String handlerTypeName, String aggregateRootTypeName) {
        return CompletableFuture.completedFuture(new AsyncTaskResult<>(AsyncTaskStatus.Success, _dict.containsKey(messageId + handlerTypeName)));
    }

    public CompletableFuture<AsyncTaskResult<Boolean>> isRecordExistAsync(String messageId1, String messageId2, String handlerTypeName, String aggregateRootTypeName) {
        return CompletableFuture.completedFuture(new AsyncTaskResult<>(AsyncTaskStatus.Success, _dict.containsKey(messageId1 + messageId2 + handlerTypeName)));
    }

    public CompletableFuture<AsyncTaskResult<Boolean>> isRecordExistAsync(String messageId1, String messageId2, String messageId3, String handlerTypeName, String aggregateRootTypeName) {
        return CompletableFuture.completedFuture(new AsyncTaskResult<>(AsyncTaskStatus.Success, _dict.containsKey(messageId1 + messageId2 + messageId3 + handlerTypeName)));
    }
}
