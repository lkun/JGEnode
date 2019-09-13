package com.kunlv.ddd.j.enode.queue.rocketmq.client.impl;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.kunlv.ddd.j.enode.queue.rocketmq.Constants;
import com.kunlv.ddd.j.enode.queue.rocketmq.client.AbstractProducer;
import com.kunlv.ddd.j.enode.queue.rocketmq.client.MQClientInitializer;
import com.kunlv.ddd.j.enode.queue.rocketmq.client.Producer;

import java.util.Properties;

public class NativeMQProducer extends AbstractProducer implements Producer {

    protected NativeMQProducer(Properties properties) {
        super(properties, new MQClientInitializer());
    }

    @Override
    protected DefaultMQProducer initProducer(Properties properties, MQClientInitializer mqClientInitializer) {
        DefaultMQProducer producer = new DefaultMQProducer();

        String producerGroup = properties.getProperty(NativePropertyKey.ProducerGroup);
        if (null == producerGroup) {
            producerGroup = Constants.DEFAULT_ENODE_PRODUCER_GROUP;
        }

        producer.setProducerGroup(producerGroup);
        producer.setNamesrvAddr(mqClientInitializer.getNameServerAddr());
        producer.setInstanceName(mqClientInitializer.buildIntanceName());

        return producer;
    }
}
