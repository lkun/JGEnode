package com.kunlv.ddd.j.enode.kafka;

import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.core.publishableexception.IPublishableException;
import com.kunlv.ddd.j.enode.core.queue.QueueMessage;
import com.kunlv.ddd.j.enode.core.queue.publishableexceptions.AbstractPublishableExceptionPublisher;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

/**
 * @author lvk618@gmail.com
 */
public class KafkaPublishableExceptionPublisher extends AbstractPublishableExceptionPublisher {
    private KafkaTemplate<String, String> producer;

    public KafkaTemplate<String, String> getProducer() {
        return producer;
    }

    public void setProducer(KafkaTemplate<String, String> producer) {
        this.producer = producer;
    }

    @Override
    public CompletableFuture<AsyncTaskResult> publishAsync(IPublishableException exception) {
        return SendMessageService.sendMessageAsync(producer, buildKafkaMessage(exception));
    }

    protected ProducerRecord<String, String> buildKafkaMessage(IPublishableException exception) {
        QueueMessage queueMessage = createExceptionMessage(exception);
        return KafkaTool.covertToProducerRecord(queueMessage);
    }
}
