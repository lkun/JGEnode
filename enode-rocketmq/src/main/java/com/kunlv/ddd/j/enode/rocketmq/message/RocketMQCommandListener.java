package com.kunlv.ddd.j.enode.rocketmq.message;

import com.kunlv.ddd.j.enode.common.serializing.JsonTool;
import com.kunlv.ddd.j.enode.core.queue.QueueMessage;
import com.kunlv.ddd.j.enode.core.queue.command.AbstractCommandListener;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author lvk618@gmail.com
 */
public class RocketMQCommandListener extends AbstractCommandListener implements MessageListenerConcurrently {
    private static Logger logger = LoggerFactory.getLogger(RocketMQCommandListener.class);

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            QueueMessage queueMessage = RocketMQTool.covertToQueueMessage(msgs);
            handle(queueMessage, message -> {
                latch.countDown();
            });
            latch.await();
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            logger.error("Ops, consume CommandMessage failed, msgs:{}", JsonTool.serialize(msgs), e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
}
