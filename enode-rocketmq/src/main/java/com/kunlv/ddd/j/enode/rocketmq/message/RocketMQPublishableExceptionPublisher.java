package com.kunlv.ddd.j.enode.rocketmq.message;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.core.publishableexception.IPublishableException;
import com.kunlv.ddd.j.enode.core.queue.QueueMessage;
import com.kunlv.ddd.j.enode.core.queue.publishableexceptions.AbstractPublishableExceptionPublisher;

import java.util.concurrent.CompletableFuture;

/**
 * @author lvk618@gmail.com
 */
public class RocketMQPublishableExceptionPublisher extends AbstractPublishableExceptionPublisher {
    private DefaultMQProducer producer;

    public DefaultMQProducer getProducer() {
        return producer;
    }

    public void setProducer(DefaultMQProducer producer) {
        this.producer = producer;
    }

    @Override
    public CompletableFuture<AsyncTaskResult> publishAsync(IPublishableException exception) {
        QueueMessage queueMessage = createExceptionMessage(exception);
        Message message = RocketMQTool.covertToProducerRecord(queueMessage);
        return SendRocketMQService.sendMessageAsync(producer, message, queueMessage.getRouteKey());
    }
}
