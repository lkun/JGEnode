package com.kunlv.ddd.j.enode.core.queue;

public interface IMessageHandler {
    void handle(QueueMessage queueMessage, IMessageContext context);
}
