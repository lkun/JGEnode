package com.kunlv.ddd.j.enode.queue.rocketmq.client;

import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.kunlv.ddd.j.enode.common.rocketmq.consumer.listener.CompletableMessageListenerConcurrently;

public interface Consumer {
    void start();

    void shutdown();

    void registerMessageListener(final MessageListenerConcurrently messageListener);

    void registerMessageListener(final CompletableMessageListenerConcurrently messageListener);

    void subscribe(final String topic, final String subExpression);

    void unsubscribe(final String topic);
}
