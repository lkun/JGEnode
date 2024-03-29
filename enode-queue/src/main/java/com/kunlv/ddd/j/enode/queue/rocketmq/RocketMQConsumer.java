package com.kunlv.ddd.j.enode.queue.rocketmq;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.kunlv.ddd.j.enode.common.logging.ENodeLogger;
import com.kunlv.ddd.j.enode.common.rocketmq.consumer.listener.CompletableConsumeConcurrentlyContext;
import com.kunlv.ddd.j.enode.queue.rocketmq.client.Consumer;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class RocketMQConsumer {
    private static final Logger _logger = ENodeLogger.getLog();
    private Consumer _consumer;
    private Set<RocketMQMessageHandler> _handlers;
    private Map<TopicTagData, RocketMQMessageHandler> _handlerDict;

    @Inject
    public RocketMQConsumer(Consumer consumer) {
        _consumer = consumer;
        _consumer.registerMessageListener(this::handle);
        _handlers = new HashSet<>();
        _handlerDict = new HashMap<>();
    }

    public void registerMessageHandler(RocketMQMessageHandler handler) {
        _handlers.add(handler);
    }

    public void subscribe(String topic, String subExpression) {
        _consumer.subscribe(topic, subExpression);
    }

    public void start() {
        _consumer.start();
    }

    public void shutdown() {
        _consumer.shutdown();
    }

    protected void handle(final List<MessageExt> msgs,
                          final CompletableConsumeConcurrentlyContext context) {

        MessageExt msg = msgs.get(0);
        String topic = msg.getTopic();
        String tag = msg.getTags();
        TopicTagData topicTagData = new TopicTagData(topic, tag);

        RocketMQMessageHandler rocketMQMessageHandler = _handlerDict.get(topicTagData);

        if (rocketMQMessageHandler == null) {
            List<RocketMQMessageHandler> handlers = _handlers.stream().filter(handler -> handler.isMatched(topicTagData)).collect(Collectors.toList());
            if (handlers.size() > 1) {
                _logger.error("Duplicate consume handler with {topic:{},tags:{}}", msg.getTopic(), msg.getTags());
                context.reconsumeLater();
//                return CompletableFuture.completedFuture(ConsumeConcurrentlyStatus.RECONSUME_LATER);
//                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }

            rocketMQMessageHandler = handlers.get(0);
            _handlerDict.put(topicTagData, rocketMQMessageHandler);
        }

        if (rocketMQMessageHandler == null) {
            _logger.error("No consume handler found with {topic:{},tags:{}}", msg.getTopic(), msg.getTags());
//            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            //return CompletableFuture.completedFuture(ConsumeConcurrentlyStatus.RECONSUME_LATER);
            context.reconsumeLater();
        } else {
            rocketMQMessageHandler.handle(msg, context);
        }
    }
}
