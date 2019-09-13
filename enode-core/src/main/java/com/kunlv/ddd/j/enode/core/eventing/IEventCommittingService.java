package com.kunlv.ddd.j.enode.core.eventing;

import com.kunlv.ddd.j.enode.core.commanding.ProcessingCommand;

public interface IEventCommittingService {
    /**
     * Commit the given aggregate's domain events to the eventstore async and publish the domain events.
     *
     * @param eventCommittingContext
     */
    void commitDomainEventAsync(EventCommittingContext eventCommittingContext);

    /**
     * Publish the given domain event stream async.
     *
     * @param processingCommand
     * @param eventStream
     */
    void publishDomainEventAsync(ProcessingCommand processingCommand, DomainEventStream eventStream);
}
