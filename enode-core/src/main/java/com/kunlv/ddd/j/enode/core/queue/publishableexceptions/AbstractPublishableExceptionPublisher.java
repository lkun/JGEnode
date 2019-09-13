package com.kunlv.ddd.j.enode.core.queue.publishableexceptions;

import com.kunlv.ddd.j.enode.common.serializing.JsonTool;
import com.kunlv.ddd.j.enode.common.utilities.Ensure;
import com.kunlv.ddd.j.enode.core.messaging.IMessagePublisher;
import com.kunlv.ddd.j.enode.core.publishableexception.IPublishableException;
import com.kunlv.ddd.j.enode.core.queue.QueueMessage;
import com.kunlv.ddd.j.enode.core.queue.QueueMessageTypeCode;
import com.kunlv.ddd.j.enode.core.queue.TopicData;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPublishableExceptionPublisher implements IMessagePublisher<IPublishableException> {
    private TopicData topicData;

    public TopicData getTopicData() {
        return topicData;
    }

    public void setTopicData(TopicData topicData) {
        this.topicData = topicData;
    }

    protected QueueMessage createExceptionMessage(IPublishableException exception) {
        Ensure.notNull(topicData, "topicData");
        Map<String, String> serializableInfo = new HashMap<>();
        exception.serializeTo(serializableInfo);
        PublishableExceptionMessage exceptionMessage = new PublishableExceptionMessage();
        exceptionMessage.setUniqueId(exception.getId());
        exceptionMessage.setExceptionType(exception.getClass().getName());
        exceptionMessage.setTimestamp(exception.getTimestamp());
        exceptionMessage.setSerializableInfo(serializableInfo);
        String data = JsonTool.serialize(exceptionMessage);
        String routeKey = exception.getId();
        QueueMessage queueMessage = new QueueMessage();
        queueMessage.setCode(QueueMessageTypeCode.ExceptionMessage.getValue());
        queueMessage.setTopic(topicData.getTopic());
        queueMessage.setTags(topicData.getTags());
        queueMessage.setBody(data);
        queueMessage.setKey(exception.getId());
        queueMessage.setRouteKey(routeKey);
        return queueMessage;
    }
}
