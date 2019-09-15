package com.kunlv.ddd.j.enode.kafka;

import com.kunlv.ddd.j.enode.core.queue.QueueMessage;
import com.kunlv.ddd.j.enode.core.queue.publishableexceptions.AbstractPublishableExceptionListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;

/**
 * @author lvk618@gmail.com
 */
public class KafkaPublishableExceptionListener extends AbstractPublishableExceptionListener implements AcknowledgingMessageListener {
    /**
     * Invoked with data from kafka.
     *
     * @param data           the data to be processed.
     * @param acknowledgment the acknowledgment.
     */
    @Override
    public void onMessage(ConsumerRecord data, Acknowledgment acknowledgment) {
        QueueMessage queueMessage = KafkaTool.covertToQueueMessage(data);
        handle(queueMessage, context -> {
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        });
    }

    /**
     * Invoked with data from kafka.
     *
     * @param data the data to be processed.
     */
    @Override
    public void onMessage(Object data) {
    }
}
