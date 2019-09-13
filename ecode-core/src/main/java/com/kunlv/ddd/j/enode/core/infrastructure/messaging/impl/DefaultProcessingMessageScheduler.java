package com.kunlv.ddd.j.enode.core.infrastructure.messaging.impl;

import com.kunlv.ddd.j.enode.core.infrastructure.messaging.*;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

public class DefaultProcessingMessageScheduler<X extends IProcessingMessage<X, Y>, Y extends IMessage> implements IProcessingMessageScheduler<X, Y> {
    private IProcessingMessageHandler<X, Y> _messageHandler;

    @Inject
    public DefaultProcessingMessageScheduler(IProcessingMessageHandler<X, Y> messageHandler) {
        _messageHandler = messageHandler;
    }

    @Override
    public void scheduleMessage(X processingMessage) {
        CompletableFuture.runAsync(() -> _messageHandler.handleAsync(processingMessage));
    }

    @Override
    public void scheduleMailbox(ProcessingMessageMailbox<X, Y> mailbox) {
        CompletableFuture.runAsync(mailbox::run);
    }
}
