package com.kunlv.ddd.j.enode.core.queue.applicationmessage;

import com.kunlv.ddd.j.enode.common.serializing.JsonTool;
import com.kunlv.ddd.j.enode.common.utilities.Ensure;
import com.kunlv.ddd.j.enode.core.applicationmessage.IApplicationMessage;
import com.kunlv.ddd.j.enode.core.messaging.IMessagePublisher;
import com.kunlv.ddd.j.enode.core.queue.QueueMessage;
import com.kunlv.ddd.j.enode.core.queue.QueueMessageTypeCode;
import com.kunlv.ddd.j.enode.core.queue.TopicData;

public abstract class AbstractApplicationMessagePublisher implements IMessagePublisher<IApplicationMessage> {
    private TopicData topicData;

    public TopicData getTopicData() {
        return topicData;
    }

    public void setTopicData(TopicData topicData) {
        this.topicData = topicData;
    }

    protected QueueMessage createApplicationMessage(IApplicationMessage message) {
        Ensure.notNull(topicData, "topicData");
        String appMessageData = JsonTool.serialize(message);
        ApplicationDataMessage appDataMessage = new ApplicationDataMessage(appMessageData, message.getClass().getName());
        String data = JsonTool.serialize(appDataMessage);
        String routeKey = message.getId();
        QueueMessage queueMessage = new QueueMessage();
        queueMessage.setBody(data);
        queueMessage.setRouteKey(routeKey);
        queueMessage.setCode(QueueMessageTypeCode.ApplicationMessage.getValue());
        queueMessage.setKey(message.getId());
        queueMessage.setTopic(topicData.getTopic());
        queueMessage.setTags(topicData.getTags());
        return queueMessage;
    }
}
