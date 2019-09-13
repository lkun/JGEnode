package com.kunlv.ddd.j.enode.core.messaging.impl;

import com.kunlv.ddd.j.enode.core.messaging.IEventProcessContext;
import com.kunlv.ddd.j.enode.core.queue.IMessageContext;
import com.kunlv.ddd.j.enode.core.queue.QueueMessage;

/**
 * @author lvk618@gmail.com
 */
public class DefaultMessageProcessContext implements IEventProcessContext {
    protected final QueueMessage queueMessage;
    protected final IMessageContext messageContext;

    public DefaultMessageProcessContext(QueueMessage queueMessage, IMessageContext messageContext) {
        this.queueMessage = queueMessage;
        this.messageContext = messageContext;
    }

    @Override
    public void notifyEventProcessed() {
        messageContext.onMessageHandled(queueMessage);
    }
}
