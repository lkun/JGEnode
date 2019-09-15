package com.kunlv.ddd.j.enode.kafka;

import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.core.applicationmessage.IApplicationMessage;
import com.kunlv.ddd.j.enode.core.queue.QueueMessage;
import com.kunlv.ddd.j.enode.core.queue.applicationmessage.AbstractApplicationMessagePublisher;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

/**
 * @author lvk618@gmail.com
 */
public class KafkaApplicationMessagePublisher extends AbstractApplicationMessagePublisher {
    private KafkaTemplate<String, String> producer;

    public KafkaTemplate<String, String> getProducer() {
        return producer;
    }

    public void setProducer(KafkaTemplate<String, String> producer) {
        this.producer = producer;
    }

    @Override
    public CompletableFuture<AsyncTaskResult> publishAsync(IApplicationMessage message) {
        return SendMessageService.sendMessageAsync(producer, buildKafkaMessage(message));
    }

    private ProducerRecord<String, String> buildKafkaMessage(IApplicationMessage message) {
        QueueMessage queueMessage = createApplicationMessage(message);
        return KafkaTool.covertToProducerRecord(queueMessage);
    }
}
