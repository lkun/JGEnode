package com.kunlv.ddd.j.enode.core.eventing.impl;

import com.kunlv.ddd.j.enode.common.serializing.IJsonSerializer;
import com.kunlv.ddd.j.enode.core.eventing.IDomainEvent;
import com.kunlv.ddd.j.enode.core.eventing.IEventSerializer;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessage;
import com.kunlv.ddd.j.enode.core.infrastructure.ITypeNameProvider;

import javax.inject.Inject;
import java.util.*;

public class DefaultEventSerializer implements IEventSerializer {
    private ITypeNameProvider _typeNameProvider;
    private IJsonSerializer _jsonSerializer;

    @Inject
    public DefaultEventSerializer(ITypeNameProvider typeNameProvider, IJsonSerializer jsonSerializer) {
        _typeNameProvider = typeNameProvider;
        _jsonSerializer = jsonSerializer;
    }

    @Override
    public Map<String, String> serialize(List<IDomainEvent> evnts) {
        Map dict = new HashMap<String, String>();

        evnts.forEach(evnt -> {
            String typeName = _typeNameProvider.getTypeName(evnt.getClass());
            String eventData = _jsonSerializer.serialize(evnt);
            dict.put(typeName, eventData);
        });

        return dict;
    }

    @Override
    public <TEvent extends IDomainEvent> List<TEvent> deserialize(Map<String, String> data, Class<TEvent> domainEventType) {
        List<TEvent> evnts = new ArrayList<>();

        data.entrySet().forEach(entry -> {
            Class eventType = _typeNameProvider.getType(entry.getKey());
            TEvent evnt = (TEvent) _jsonSerializer.deserialize(entry.getValue(), eventType);

            evnts.add(evnt);
        });

        Collections.sort(evnts, Comparator.comparingInt(IMessage::sequence)
        );

        return evnts;
    }
}
