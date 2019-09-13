package com.kunlv.ddd.j.enode.core.queue.publishableexceptions;

import com.kunlv.ddd.j.enode.common.exception.ENodeRuntimeException;
import com.kunlv.ddd.j.enode.common.serializing.JsonTool;
import com.kunlv.ddd.j.enode.core.infrastructure.ITypeNameProvider;
import com.kunlv.ddd.j.enode.core.messaging.IMessageDispatcher;
import com.kunlv.ddd.j.enode.core.publishableexception.IPublishableException;
import com.kunlv.ddd.j.enode.core.queue.IMessageContext;
import com.kunlv.ddd.j.enode.core.queue.IMessageHandler;
import com.kunlv.ddd.j.enode.core.queue.QueueMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractPublishableExceptionListener implements IMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractPublishableExceptionListener.class);
    @Autowired
    protected ITypeNameProvider typeNameProvider;

    @Autowired
    private IMessageDispatcher messageDispatcher;

    public AbstractPublishableExceptionListener setTypeNameProvider(ITypeNameProvider typeNameProvider) {
        this.typeNameProvider = typeNameProvider;
        return this;
    }

    @Override
    public void handle(QueueMessage queueMessage, IMessageContext context) {
        PublishableExceptionMessage exceptionMessage = JsonTool.deserialize(queueMessage.getBody(), PublishableExceptionMessage.class);
        Class exceptionType = typeNameProvider.getType(exceptionMessage.getExceptionType());
        IPublishableException exception;
        try {
            exception = (IPublishableException) exceptionType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ENodeRuntimeException(e);
        }
        exception.setId(exceptionMessage.getUniqueId());
        exception.setTimestamp(exceptionMessage.getTimestamp());
        exception.restoreFrom(exceptionMessage.getSerializableInfo());
        if (logger.isDebugEnabled()) {
            logger.debug("ENode exception message received, messageId: {}", exceptionMessage.getUniqueId());
        }
        messageDispatcher.dispatchMessageAsync(exception).thenAccept(x -> {
            context.onMessageHandled(queueMessage);
        });
    }
}
