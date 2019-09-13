package com.kunlv.ddd.j.enode.queue.rocketmq;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.kunlv.ddd.j.enode.common.rocketmq.consumer.listener.CompletableConsumeConcurrentlyContext;

public interface RocketMQMessageHandler {
    boolean isMatched(TopicTagData topicTagData);

    void handle(MessageExt message, CompletableConsumeConcurrentlyContext context);
}
