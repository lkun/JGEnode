package com.kunlv.ddd.j.enode.core.eventing.impl;

import com.google.common.collect.Maps;
import com.kunlv.ddd.j.enode.common.serializing.JsonTool;
import com.kunlv.ddd.j.enode.core.eventing.IDomainEvent;
import com.kunlv.ddd.j.enode.core.eventing.IEventSerializer;
import com.kunlv.ddd.j.enode.core.infrastructure.ITypeNameProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lvk618@gmail.com
 */
public class DefaultEventSerializer implements IEventSerializer {
    @Autowired
    private ITypeNameProvider typeNameProvider;

    public DefaultEventSerializer setTypeNameProvider(ITypeNameProvider typeNameProvider) {
        this.typeNameProvider = typeNameProvider;
        return this;
    }

    @Override
    public Map<String, String> serialize(List<IDomainEvent> evnts) {
        LinkedHashMap<String, String> dict = Maps.newLinkedHashMap();
        evnts.forEach(evnt -> {
            String typeName = typeNameProvider.getTypeName(evnt.getClass());
            String eventData = JsonTool.serialize(evnt);
            dict.put(typeName, eventData);
        });
        return dict;
    }

    @Override
    public <TEvent extends IDomainEvent> List<TEvent> deserialize(Map<String, String> data, Class<TEvent> domainEventType) {
        List<TEvent> evnts = new ArrayList<>();
        data.entrySet().forEach(entry -> {
            Class eventType = typeNameProvider.getType(entry.getKey());
            TEvent evnt = (TEvent) JsonTool.deserialize(entry.getValue(), eventType);
            evnts.add(evnt);
        });
        return evnts;
    }
}
