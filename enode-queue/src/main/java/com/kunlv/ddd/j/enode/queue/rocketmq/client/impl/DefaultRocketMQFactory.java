package com.kunlv.ddd.j.enode.queue.rocketmq.client.impl;

import com.kunlv.ddd.j.enode.queue.rocketmq.client.Consumer;
import com.kunlv.ddd.j.enode.queue.rocketmq.client.Producer;
import com.kunlv.ddd.j.enode.queue.rocketmq.client.RocketMQFactory;

import java.util.Properties;

public class DefaultRocketMQFactory implements RocketMQFactory {

    @Override
    public Producer createProducer(Properties properties) {
        return null;
    }

    @Override
    public Consumer createPushConsumer(Properties properties) {
        return null;
    }
}
