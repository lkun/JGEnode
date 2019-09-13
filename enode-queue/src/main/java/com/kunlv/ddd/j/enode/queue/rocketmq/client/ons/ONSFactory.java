package com.kunlv.ddd.j.enode.queue.rocketmq.client.ons;

import com.kunlv.ddd.j.enode.queue.rocketmq.client.Consumer;
import com.kunlv.ddd.j.enode.queue.rocketmq.client.Producer;
import com.kunlv.ddd.j.enode.queue.rocketmq.client.RocketMQFactory;

import java.util.Properties;

public class ONSFactory implements RocketMQFactory {
    @Override
    public Producer createProducer(Properties properties) {
        return new ONSProducerImpl(properties);
    }

    @Override
    public Consumer createPushConsumer(Properties properties) {
        return new ONSConsumerImpl(properties);
    }
}
