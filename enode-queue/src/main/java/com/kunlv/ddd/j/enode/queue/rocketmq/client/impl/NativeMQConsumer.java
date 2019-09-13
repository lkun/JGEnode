package com.kunlv.ddd.j.enode.queue.rocketmq.client.impl;

import com.kunlv.ddd.j.enode.common.rocketmq.consumer.CompletableDefaultMQPushConsumer;
import com.kunlv.ddd.j.enode.queue.rocketmq.Constants;
import com.kunlv.ddd.j.enode.queue.rocketmq.client.AbstractConsumer;
import com.kunlv.ddd.j.enode.queue.rocketmq.client.Consumer;
import com.kunlv.ddd.j.enode.queue.rocketmq.client.MQClientInitializer;

import java.util.Properties;

public class NativeMQConsumer extends AbstractConsumer implements Consumer {

    protected NativeMQConsumer(Properties properties) {
        super(properties, new MQClientInitializer());
    }

    @Override
    protected CompletableDefaultMQPushConsumer initConsumer(Properties properties, MQClientInitializer mqClientInitializer) {
        CompletableDefaultMQPushConsumer consumer = new CompletableDefaultMQPushConsumer();

        String consumerGroup = properties.getProperty(NativePropertyKey.ConsumerGroup);
        if (null == consumerGroup) {
            consumerGroup = Constants.DEFAULT_ENODE_CONSUMER_GROUP;
        }

        consumer.setConsumerGroup(consumerGroup);
        consumer.setNamesrvAddr(mqClientInitializer.getNameServerAddr());
        consumer.setInstanceName(mqClientInitializer.buildIntanceName());

        return consumer;
    }
}
