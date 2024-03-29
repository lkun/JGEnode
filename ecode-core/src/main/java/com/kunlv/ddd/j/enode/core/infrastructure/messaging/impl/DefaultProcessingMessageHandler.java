package com.kunlv.ddd.j.enode.core.infrastructure.messaging.impl;

import  com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessage;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessageDispatcher;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IProcessingMessage;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IProcessingMessageHandler;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

public class DefaultProcessingMessageHandler<X extends IProcessingMessage<X, Y>, Y extends IMessage> implements IProcessingMessageHandler<X, Y> {
    private final IMessageDispatcher _dispatcher;

    @Inject
    public DefaultProcessingMessageHandler(IMessageDispatcher dispatcher) {
        _dispatcher = dispatcher;
    }

    public void handleAsync(X processingMessage) {
        CompletableFuture<AsyncTaskResult> asyncTaskResultCompletableFuture = _dispatcher.dispatchMessageAsync(processingMessage.getMessage());
        asyncTaskResultCompletableFuture.thenRun(() ->
                processingMessage.complete()
        );
    }
}
