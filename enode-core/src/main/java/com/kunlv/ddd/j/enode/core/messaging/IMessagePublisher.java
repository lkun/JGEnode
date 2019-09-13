package com.kunlv.ddd.j.enode.core.messaging;

import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;

import java.util.concurrent.CompletableFuture;

public interface IMessagePublisher<TMessage extends IMessage> {
    CompletableFuture<AsyncTaskResult> publishAsync(TMessage message);
}
