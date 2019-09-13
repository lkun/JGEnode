package com.kunlv.ddd.j.enode.core.applicationmessage;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.kunlv.ddd.j.enode.common.rocketmq.consumer.listener.CompletableConsumeConcurrentlyContext;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessageProcessContext;

public class RocketMQProcessContext implements IMessageProcessContext {
    protected final MessageExt _queueMessage;
    protected final CompletableConsumeConcurrentlyContext _messageContext;

    public RocketMQProcessContext(MessageExt queueMessage, CompletableConsumeConcurrentlyContext messageContext) {
        _queueMessage = queueMessage;
        _messageContext = messageContext;
    }

    public void notifyMessageProcessed() {
        _messageContext.onMessageHandled();
    }
}
