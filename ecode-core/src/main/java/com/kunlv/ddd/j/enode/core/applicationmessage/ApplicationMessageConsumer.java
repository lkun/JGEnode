package com.kunlv.ddd.j.enode.core.applicationmessage;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.kunlv.ddd.j.enode.common.logging.ENodeLogger;
import com.kunlv.ddd.j.enode.common.rocketmq.consumer.listener.CompletableConsumeConcurrentlyContext;
import com.kunlv.ddd.j.enode.common.serializing.IJsonSerializer;
import com.kunlv.ddd.j.enode.common.utilities.BitConverter;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IApplicationMessage;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessageProcessor;
import com.kunlv.ddd.j.enode.core.infrastructure.ITypeNameProvider;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.ProcessingApplicationMessage;
import com.kunlv.ddd.j.enode.queue.rocketmq.ITopicProvider;
import com.kunlv.ddd.j.enode.queue.rocketmq.RocketMQConsumer;
import com.kunlv.ddd.j.enode.queue.rocketmq.RocketMQMessageHandler;
import com.kunlv.ddd.j.enode.queue.rocketmq.TopicTagData;
import org.slf4j.Logger;

import javax.inject.Inject;

public class ApplicationMessageConsumer {

    private static final Logger _logger = ENodeLogger.getLog();

    private final RocketMQConsumer _consumer;
    private final IJsonSerializer _jsonSerializer;
    private final ITopicProvider<IApplicationMessage> _messageTopicProvider;
    private final ITypeNameProvider _typeNameProvider;
    private final IMessageProcessor<ProcessingApplicationMessage, IApplicationMessage> _processor;

    @Inject
    public ApplicationMessageConsumer(RocketMQConsumer consumer, IJsonSerializer jsonSerializer,
                                      ITopicProvider<IApplicationMessage> messageITopicProvider, ITypeNameProvider typeNameProvider,
                                      IMessageProcessor<ProcessingApplicationMessage, IApplicationMessage> processor) {
        _consumer = consumer;
        _jsonSerializer = jsonSerializer;
        _messageTopicProvider = messageITopicProvider;
        _typeNameProvider = typeNameProvider;
        _processor = processor;
    }

    public ApplicationMessageConsumer start() {
        _consumer.registerMessageHandler(new RocketMQMessageHandler() {
            @Override
            public boolean isMatched(TopicTagData topicTagData) {
                return _messageTopicProvider.getAllSubscribeTopics().contains(topicTagData);
            }

            @Override
            public void handle(MessageExt message, CompletableConsumeConcurrentlyContext context) {
                ApplicationMessageConsumer.this.handle(message, context);
            }
        });
        return this;
    }

    public ApplicationMessageConsumer shutdown() {
        return this;
    }

    void handle(final MessageExt msg, final CompletableConsumeConcurrentlyContext context) {
        ApplicationDataMessage appDataMessage = _jsonSerializer.deserialize(BitConverter.toString(msg.getBody()), ApplicationDataMessage.class);
        Class applicationMessageType;

        try {
            applicationMessageType = _typeNameProvider.getType(appDataMessage.getApplicationMessageType());
        } catch (Exception e) {
            _logger.warn("Consume applicatio message exception:", e);
            context.onMessageHandled();
            return;
        }

        IApplicationMessage message = (IApplicationMessage) _jsonSerializer.deserialize(appDataMessage.getApplicationMessageData(), applicationMessageType);
        RocketMQProcessContext processContext = new RocketMQProcessContext(msg, context);
        ProcessingApplicationMessage processingMessage = new ProcessingApplicationMessage(message, processContext);
        _logger.info("ENode application message received, messageId: {}, routingKey: {}", message.id(), message.getRoutingKey());
        _processor.process(processingMessage);
//        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    public RocketMQConsumer getConsumer() {
        return _consumer;
    }
}
