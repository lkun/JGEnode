package com.kunlv.ddd.j.enode.queue.rocketmq;

import java.util.Collection;

public interface ITopicProvider<T> {
    TopicTagData getPublishTopic(T source);

    Collection<TopicTagData> getAllSubscribeTopics();
}
