package com.kunlv.ddd.j.enode.core.domain;

import com.kunlv.ddd.j.enode.core.eventing.DomainEventStream;
import com.kunlv.ddd.j.enode.core.eventing.IDomainEvent;

import java.util.List;

public interface IAggregateRoot {
    String uniqueId();

    int version();

    List<IDomainEvent> getChanges();

    void acceptChanges(int newVersion);

    void replayEvents(List<DomainEventStream> eventStreams);
}
