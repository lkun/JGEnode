package com.kunlv.ddd.j.enode.example.consumer.commandhandles;

import com.kunlv.ddd.j.enode.core.queue.TopicData;
import com.kunlv.ddd.j.enode.example.QueueProperties;
import com.kunlv.ddd.j.enode.rocketmq.message.RocketMQApplicationMessagePublisher;
import com.kunlv.ddd.j.enode.rocketmq.message.RocketMQCommandListener;
import com.kunlv.ddd.j.enode.rocketmq.message.RocketMQDomainEventPublisher;
import com.kunlv.ddd.j.enode.rocketmq.message.RocketMQPublishableExceptionPublisher;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.context.annotation.Bean;

public class RocketMQCommandConfig {
    @Bean
    public RocketMQCommandListener commandListener() {
        return new RocketMQCommandListener();
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public DefaultMQPushConsumer defaultMQPushConsumer(RocketMQCommandListener commandListener) throws MQClientException {
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer();
        defaultMQPushConsumer.setConsumerGroup(QueueProperties.DEFAULT_CONSUMER_GROUP3);
        defaultMQPushConsumer.setNamesrvAddr(QueueProperties.NAMESRVADDR);
        defaultMQPushConsumer.subscribe(QueueProperties.COMMAND_TOPIC, "*");
        defaultMQPushConsumer.setMessageListener(commandListener);
        return defaultMQPushConsumer;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public DefaultMQProducer defaultMQProducer() {
        DefaultMQProducer producer = new DefaultMQProducer();
        producer.setNamesrvAddr(QueueProperties.NAMESRVADDR);
        producer.setProducerGroup(QueueProperties.DEFAULT_PRODUCER_GROUP);
        return producer;
    }

    @Bean
    public RocketMQDomainEventPublisher rocketMQDomainEventPublisher(DefaultMQProducer defaultMQProducer) {
        RocketMQDomainEventPublisher domainEventPublisher = new RocketMQDomainEventPublisher();
        domainEventPublisher.setProducer(defaultMQProducer);
        domainEventPublisher.setTopicData(new TopicData(QueueProperties.EVENT_TOPIC, "*"));
        return domainEventPublisher;
    }

    /**
     * 应用消息生产者，复用生产者实例发送到不同topic中
     */
    @Bean
    public RocketMQApplicationMessagePublisher rocketMQApplicationMessagePublisher(DefaultMQProducer defaultMQProducer) {
        RocketMQApplicationMessagePublisher applicationMessagePublisher = new RocketMQApplicationMessagePublisher();
        applicationMessagePublisher.setProducer(defaultMQProducer);
        applicationMessagePublisher.setTopicData(new TopicData(QueueProperties.APPLICATION_TOPIC, "*"));
        return applicationMessagePublisher;
    }

    /**
     * 异常消息生产者，复用生产者实例发送到不同topic中
     */
    @Bean
    public RocketMQPublishableExceptionPublisher rocketMQPublishableExceptionPublisher(DefaultMQProducer defaultMQProducer) {
        RocketMQPublishableExceptionPublisher exceptionPublisher = new RocketMQPublishableExceptionPublisher();
        exceptionPublisher.setProducer(defaultMQProducer);
        exceptionPublisher.setTopicData(new TopicData(QueueProperties.EXCEPTION_TOPIC, "*"));
        return exceptionPublisher;
    }
}
