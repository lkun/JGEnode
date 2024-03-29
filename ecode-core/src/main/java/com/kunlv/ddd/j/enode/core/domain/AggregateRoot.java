package com.kunlv.ddd.j.enode.core.domain;

import com.kunlv.ddd.j.enode.common.function.Action2;
import com.kunlv.ddd.j.enode.core.eventing.DomainEventStream;
import com.kunlv.ddd.j.enode.core.eventing.IDomainEvent;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot<TAggregateRootId> implements IAggregateRoot {
    @Inject
    private static IAggregateRootInternalHandlerProvider _eventHandlerProvider;
    private int _version;
    private List<IDomainEvent> _uncommittedEvents;
    protected TAggregateRootId _id;

    public TAggregateRootId id() {
        return _id;
    }

    protected AggregateRoot() {
        _uncommittedEvents = new ArrayList<>();
    }

    protected AggregateRoot(TAggregateRootId id) {
        this();
        if (id == null) {
            throw new IllegalArgumentException("id");
        }
        _id = id;
    }

    protected AggregateRoot(TAggregateRootId id, int version) {
        this(id);
        if (version < 0) {
            throw new IllegalArgumentException(String.format("Version cannot small than zero, aggregateRootId: %s, version: %d", id, version));
        }
        _version = version;
    }

    protected void applyEvent(IDomainEvent<TAggregateRootId> domainEvent) {
        if (domainEvent == null) {
            throw new NullPointerException("domainEvent");
        }

        if (_id == null) {
            throw new RuntimeException("Aggregate root id cannot be null.");
        }
        domainEvent.setAggregateRootId(_id);
        domainEvent.setVersion(_version + 1);
        handleEvent(domainEvent);
        appendUncommittedEvent(domainEvent);
    }

    protected void applyEvents(IDomainEvent<TAggregateRootId>[] domainEvents) {
        for (int i = 0, len = domainEvents.length; i < len; i++) {
            applyEvent(domainEvents[i]);
        }
    }

    private void handleEvent(IDomainEvent domainEvent) {
        Action2<IAggregateRoot, IDomainEvent> handler = _eventHandlerProvider.getInternalEventHandler(getClass(), domainEvent.getClass());
        if (handler == null) {
            throw new RuntimeException(String.format("Could not find event handler for [%s] of [%s]", domainEvent.getClass().getName(), getClass().getName()));
        }
        if (this._id == null && domainEvent.version() == 1) {
            this._id = (TAggregateRootId) domainEvent.aggregateRootId();
        }
        try {
            handler.apply(this, domainEvent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void appendUncommittedEvent(IDomainEvent<TAggregateRootId> domainEvent) {
        if (_uncommittedEvents == null) {
            _uncommittedEvents = new ArrayList<>();
        }
        if (_uncommittedEvents.stream().anyMatch(x -> x.getClass().equals(domainEvent.getClass()))) {
            throw new UnsupportedOperationException(String.format("Cannot apply duplicated domain event type: %s, current aggregateRoot type: %s, id: %s", domainEvent.getTypeName(), this.getClass().getName(), _id));
        }
        _uncommittedEvents.add(domainEvent);
    }

    private void verifyEvent(DomainEventStream eventStream) {
        if (eventStream.version() > 1 && !eventStream.aggregateRootId().equals(this.uniqueId())) {
            throw new UnsupportedOperationException(String.format("Invalid domain event stream, aggregateRootId:%s, expected aggregateRootId:%s, type:%s", eventStream.aggregateRootId(), this.uniqueId(), this.getClass().getName()));
        }
        if (eventStream.version() != this.version() + 1) {
            throw new UnsupportedOperationException(String.format("Invalid domain event stream, version:%d, expected version:%d, current aggregateRoot type:%s, id:%s", eventStream.version(), this.version(), this.getClass().getName(), this.uniqueId()));
        }
    }

    @Override
    public String uniqueId() {
        if (_id != null)
            return _id.toString();

        return null;
    }

    @Override
    public int version() {
        return _version;
    }

    @Override
    public List<IDomainEvent> getChanges() {
        if (_uncommittedEvents == null)
            return Collections.emptyList();

        return Collections.unmodifiableList(_uncommittedEvents);
    }

    @Override
    public void acceptChanges(int newVersion) {
        if (_version + 1 != newVersion) {
            throw new UnsupportedOperationException(String.format("Cannot accept invalid version: %d, expect version: %d, current aggregateRoot type: %s, id: %s", newVersion, _version + 1, this.getClass().getName(), _id));
        }
        _version = newVersion;
        _uncommittedEvents.clear();
    }

    @Override
    public void replayEvents(List<DomainEventStream> eventStreams) {
        if (eventStreams == null) return;

        eventStreams.forEach(eventStream -> {
            verifyEvent(eventStream);
            eventStream.events().forEach(domainEvent -> handleEvent(domainEvent));

            _version = eventStream.version();
        });
    }
}
