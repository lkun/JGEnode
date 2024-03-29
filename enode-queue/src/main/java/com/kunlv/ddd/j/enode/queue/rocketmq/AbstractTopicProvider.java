package com.kunlv.ddd.j.enode.queue.rocketmq;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTopicProvider<T> implements ITopicProvider<T> {
    private Map<Class, TopicTagData> _topicDict = new HashMap<>();

    @Override
    public TopicTagData getPublishTopic(T source) {
        return _topicDict.get(source.getClass());
    }


    @Override
    public Collection<TopicTagData> getAllSubscribeTopics() {
        return _topicDict.values();
    }

    protected Collection<Class> getAllTypes() {
        return _topicDict.keySet();
    }

    protected void registerTopic(TopicTagData topic, Class[] types) {
        if (types == null || types.length == 0)
            return;

        for (int i = 0, len = types.length; i < len; i++) {
            _topicDict.put(types[i], topic);
        }
    }
}
