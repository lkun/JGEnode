package com.kunlv.ddd.j.enode.core.eventing;

import com.alibaba.rocketmq.common.message.Message;
import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.common.serializing.IJsonSerializer;
import com.kunlv.ddd.j.enode.common.utilities.BitConverter;
import com.kunlv.ddd.j.enode.common.utilities.Ensure;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessagePublisher;
import com.kunlv.ddd.j.enode.queue.rocketmq.ITopicProvider;
import com.kunlv.ddd.j.enode.queue.rocketmq.RocketMQMessageTypeCode;
import com.kunlv.ddd.j.enode.queue.rocketmq.SendQueueMessageService;
import com.kunlv.ddd.j.enode.queue.rocketmq.TopicTagData;
import com.kunlv.ddd.j.enode.queue.rocketmq.client.Producer;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

public class DomainEventPublisher implements IMessagePublisher<DomainEventStreamMessage> {
    private final IJsonSerializer _jsonSerializer;
    private final ITopicProvider<IDomainEvent> _eventTopicProvider;
    private final IEventSerializer _eventSerializer;
    private final Producer _producer;
    private final SendQueueMessageService _sendMessageService;

    public Producer getProducer() {
        return _producer;
    }

    @Inject
    public DomainEventPublisher(Producer producer, IJsonSerializer jsonSerializer,
                                ITopicProvider<IDomainEvent> eventTopicProvider,
                                IEventSerializer eventSerializer,
                                SendQueueMessageService sendQueueMessageService) {
        _producer = producer;
        _jsonSerializer = jsonSerializer;
        _eventTopicProvider = eventTopicProvider;
        _eventSerializer = eventSerializer;

        _sendMessageService = sendQueueMessageService;
    }

    public DomainEventPublisher start() {
        return this;
    }

    public DomainEventPublisher shutdown() {
        return this;
    }

    public CompletableFuture<AsyncTaskResult> publishAsync(DomainEventStreamMessage eventStream) {
        Message message = createRocketMQMessage(eventStream);
        return _sendMessageService.sendMessageAsync(_producer, message, eventStream.getRoutingKey() == null ? eventStream.aggregateRootId() : eventStream.getRoutingKey(), eventStream.id(), String.valueOf(eventStream.version()));
    }

    private Message createRocketMQMessage(DomainEventStreamMessage eventStream) {
        Ensure.notNull(eventStream.aggregateRootId(), "aggregateRootId");
        EventStreamMessage eventMessage = createEventMessage(eventStream);
        TopicTagData topicTagData = _eventTopicProvider.getPublishTopic(null);
        String data = _jsonSerializer.serialize(eventMessage);
        String key = buildRocketMQMessageKey(eventStream);

        byte[] body = BitConverter.getBytes(data);

        return new Message(topicTagData.getTopic(),
                topicTagData.getTag(),
                key,
                RocketMQMessageTypeCode.DomainEventStreamMessage.getValue(), body, true);
    }

    private String buildRocketMQMessageKey(DomainEventStreamMessage eventStreamMessage) {
        return String.format("%s %s %s",
                eventStreamMessage.id(), //事件流唯一id
                "event_agg_" + eventStreamMessage.aggregateRootStringId(), //聚合根id
                "event_cmd_" + eventStreamMessage.getCommandId() //命令id
        );
    }

    private EventStreamMessage createEventMessage(DomainEventStreamMessage eventStream) {
        EventStreamMessage message = new EventStreamMessage();

        message.setId(eventStream.id());

        message.setCommandId(eventStream.getCommandId());
        message.setAggregateRootTypeName(eventStream.aggregateRootTypeName());
        message.setAggregateRootId(eventStream.aggregateRootId());
        message.setTimestamp(eventStream.timestamp());
        message.setVersion(eventStream.version());
        message.setEvents(_eventSerializer.serialize(eventStream.getEvents()));
        message.setItems(eventStream.getItems());

        return message;
    }
}
