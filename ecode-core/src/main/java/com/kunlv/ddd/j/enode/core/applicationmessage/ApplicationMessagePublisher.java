package com.kunlv.ddd.j.enode.core.applicationmessage;

import com.alibaba.rocketmq.common.message.Message;
import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.common.serializing.IJsonSerializer;
import com.kunlv.ddd.j.enode.common.utilities.BitConverter;
import com.kunlv.ddd.j.enode.core.command.CommandService;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IApplicationMessage;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessagePublisher;
import com.kunlv.ddd.j.enode.core.infrastructure.ITypeNameProvider;
import com.kunlv.ddd.j.enode.queue.rocketmq.ITopicProvider;
import com.kunlv.ddd.j.enode.queue.rocketmq.RocketMQMessageTypeCode;
import com.kunlv.ddd.j.enode.queue.rocketmq.SendQueueMessageService;
import com.kunlv.ddd.j.enode.queue.rocketmq.TopicTagData;
import com.kunlv.ddd.j.enode.queue.rocketmq.client.Producer;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

public class ApplicationMessagePublisher implements IMessagePublisher<IApplicationMessage> {

    private final IJsonSerializer _jsonSerializer;
    private final ITopicProvider<IApplicationMessage> _messageTopicProvider;
    private final ITypeNameProvider _typeNameProvider;
    private final Producer _producer;
    private final SendQueueMessageService _sendMessageService;

    public Producer getProducer() {
        return _producer;
    }

    @Inject
    public ApplicationMessagePublisher(Producer producer, IJsonSerializer jsonSerializer,
                                       ITopicProvider<IApplicationMessage> messageITopicProvider,
                                       ITypeNameProvider typeNameProvider,
                                       SendQueueMessageService sendQueueMessageService) {
        _producer = producer;
        _jsonSerializer = jsonSerializer;
        _messageTopicProvider = messageITopicProvider;
        _typeNameProvider = typeNameProvider;
        _sendMessageService = sendQueueMessageService;
    }

    public ApplicationMessagePublisher start() {
        return this;
    }

    public ApplicationMessagePublisher shutdown() {
        return this;
    }

    public CompletableFuture<AsyncTaskResult> publishAsync(IApplicationMessage message) {
        Message queueMessage = createEQueueMessage(message);
        return _sendMessageService.sendMessageAsync(_producer, queueMessage, message.getRoutingKey() == null ? message.id() : message.getRoutingKey(), message.id(), null);
    }

    private Message createEQueueMessage(IApplicationMessage message) {
        TopicTagData topicTagData = _messageTopicProvider.getPublishTopic(message);
        String appMessageData = _jsonSerializer.serialize(message);
        ApplicationDataMessage appDataMessage = new ApplicationDataMessage(appMessageData, message.getClass().getName());

        String data = _jsonSerializer.serialize(appDataMessage);

        Message mqMessage =  new Message(topicTagData.getTopic(), //topic
                //_typeNameProvider.getTypeName(message.getClass()), //tags
                topicTagData.getTag(), //tag
                message.id(), // keys
                RocketMQMessageTypeCode.ApplicationMessage.getValue(), // flag
                BitConverter.getBytes(data), // body
                true);

        if (message.getStartDeliverTime() > 0) {
            mqMessage.putUserProperty(CommandService.RocketMQSystemPropKey.STARTDELIVERTIME, String.valueOf(message.getStartDeliverTime()));
        }

        return mqMessage;
    }
}
