package com.kunlv.ddd.j.enode.core.eventing;

import com.kunlv.ddd.j.enode.core.infrastructure.messaging.SequenceMessage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DomainEventStreamMessage extends SequenceMessage<String> {

    private String commandId;
    private Map<String, String> items;
    private List<IDomainEvent> events;

    public DomainEventStreamMessage() {
    }

    public DomainEventStreamMessage(String commandId, String aggregateRootId, int version, String aggregateRootTypeName, List<IDomainEvent> events, Map<String, String> items) {
        this.commandId = commandId;
        setAggregateRootId(aggregateRootId);
        setVersion(version);
        setAggregateRootTypeName(aggregateRootTypeName);
        this.events = events;
        this.items = items;
    }

    @Override
    public String toString() {
        return String.format("[MessageId=%s,CommandId=%s,AggregateRootId=%s,AggregateRootTypeName=%s,Version=%d,Events=%s,Items=%s]",
                id(),
                commandId,
                aggregateRootId(),
                aggregateRootTypeName(),
                version(),
                String.join("|", events.stream().map(x -> x.getClass().getName()).collect(Collectors.toList())),
                String.join("|", items.entrySet().stream().map(x -> x.getKey() + ":" + x.getValue()).collect(Collectors.toList())));
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public Map<String, String> getItems() {
        return items;
    }

    public void setItems(Map<String, String> items) {
        this.items = items;
    }

    public List<IDomainEvent> getEvents() {
        return events;
    }

    public void setEvents(List<IDomainEvent> events) {
        this.events = events;
    }

    /*public String getTag() {
        if (events.size() == 1) {
            return events.get(0).getClass().getName();
        } else {
            return DOMAIN_EVENT_TAG;
        }
    }*/
}
