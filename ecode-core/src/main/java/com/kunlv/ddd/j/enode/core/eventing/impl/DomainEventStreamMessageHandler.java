package com.kunlv.ddd.j.enode.core.eventing.impl;

import com.kunlv.ddd.j.enode.core.ENode;
import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.common.io.IOHelper;
import com.kunlv.ddd.j.enode.core.eventing.DomainEventStreamMessage;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessageDispatcher;
import com.kunlv.ddd.j.enode.core.infrastructure.IPublishedVersionStore;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.ProcessingDomainEventStreamMessage;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.impl.AbstractSequenceProcessingMessageHandler;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

public class DomainEventStreamMessageHandler extends AbstractSequenceProcessingMessageHandler<ProcessingDomainEventStreamMessage, DomainEventStreamMessage> {
    private final IMessageDispatcher _dispatcher;

    @Inject
    public DomainEventStreamMessageHandler(IPublishedVersionStore publishedVersionStore, IMessageDispatcher dispatcher, IOHelper ioHelper) {
        super(publishedVersionStore, ioHelper);
        _dispatcher = dispatcher;
    }

    @Override
    public String getName() {
        return ENode.getInstance().getSetting().getDomainEventStreamMessageHandlerName();
    }

    @Override
    protected CompletableFuture<AsyncTaskResult> dispatchProcessingMessageAsync(ProcessingDomainEventStreamMessage processingMessage) {
        return _dispatcher.dispatchMessagesAsync(processingMessage.getMessage().getEvents());
    }
}
