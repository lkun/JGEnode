package com.kunlv.ddd.j.enode.core.eventing;

import com.kunlv.ddd.j.enode.core.commanding.IProcessingCommandHandler;
import com.kunlv.ddd.j.enode.core.commanding.ProcessingCommand;

public interface IEventService {
    /**
     * Set the command executor for command retring.
     *
     * @param processingCommandHandler
     */
    void setProcessingCommandHandler(IProcessingCommandHandler processingCommandHandler);

    /**
     * Commit the given aggregate's domain events to the eventstore async and publish the domain events.
     *
     * @param context
     */
    void commitDomainEventAsync(EventCommittingContext context);

    /**
     * Publish the given domain event stream async.
     *
     * @param processingCommand
     * @param eventStream
     */
    void publishDomainEventAsync(ProcessingCommand processingCommand, DomainEventStream eventStream);

    /**
     * Start background tasks.
     */
    void start();

    /**
     * Stop background tasks.
     */
    void stop();
}
