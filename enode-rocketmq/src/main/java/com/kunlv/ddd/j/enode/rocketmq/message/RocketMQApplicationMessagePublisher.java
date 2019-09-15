package com.kunlv.ddd.j.enode.rocketmq.message;

import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.core.applicationmessage.IApplicationMessage;
import com.kunlv.ddd.j.enode.core.queue.QueueMessage;
import com.kunlv.ddd.j.enode.core.queue.applicationmessage.AbstractApplicationMessagePublisher;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.CompletableFuture;

/**
 * @author lvk618@gmail.com
 */
public class RocketMQApplicationMessagePublisher extends AbstractApplicationMessagePublisher {
    private DefaultMQProducer producer;

    public DefaultMQProducer getProducer() {
        return producer;
    }

    public void setProducer(DefaultMQProducer producer) {
        this.producer = producer;
    }

    @Override
    public CompletableFuture<AsyncTaskResult> publishAsync(IApplicationMessage message) {
        QueueMessage queueMessage = createApplicationMessage(message);
        Message msg = RocketMQTool.covertToProducerRecord(queueMessage);
        return SendRocketMQService.sendMessageAsync(producer, msg, queueMessage.getRouteKey());
    }
}