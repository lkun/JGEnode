package com.kunlv.ddd.j.enode.example.web;

import com.kunlv.ddd.j.enode.rocketmq.message.RocketMQCommandService;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import com.kunlv.ddd.j.enode.core.queue.TopicData;
import com.kunlv.ddd.j.enode.example.QueueProperties;
import org.springframework.context.annotation.Bean;

public class RocketMQConfig {
    @Bean
    public RocketMQCommandService rocketMQCommandService(DefaultMQProducer producer) {
        RocketMQCommandService rocketMQCommandService = new RocketMQCommandService();
        rocketMQCommandService.setDefaultMQProducer(producer);
        TopicData topicData = new TopicData(QueueProperties.COMMAND_TOPIC, "*");
        rocketMQCommandService.setTopicData(topicData);
        return rocketMQCommandService;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public DefaultMQProducer commandProducer() {
        DefaultMQProducer producer = new DefaultMQProducer();
        producer.setNamesrvAddr(QueueProperties.NAMESRVADDR);
        producer.setProducerGroup(QueueProperties.DEFAULT_PRODUCER_GROUP);
        return producer;
    }
}
