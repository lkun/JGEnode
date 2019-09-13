package com.kunlv.ddd.j.enode.core.eventing;

import com.kunlv.ddd.j.enode.core.commanding.ProcessingCommand;
import com.kunlv.ddd.j.enode.core.domain.IAggregateRoot;

/**
 * @author lvk618@gmail.com
 */
public class EventCommittingContext {
    private EventCommittingContextMailBox mailBox;
    private IAggregateRoot aggregateRoot;
    private DomainEventStream eventStream;
    private ProcessingCommand processingCommand;

    public EventCommittingContext(IAggregateRoot aggregateRoot, DomainEventStream eventStream, ProcessingCommand processingCommand) {
        this.aggregateRoot = aggregateRoot;
        this.eventStream = eventStream;
        this.processingCommand = processingCommand;
    }

    public IAggregateRoot getAggregateRoot() {
        return aggregateRoot;
    }

    public DomainEventStream getEventStream() {
        return eventStream;
    }

    public ProcessingCommand getProcessingCommand() {
        return processingCommand;
    }

    public EventCommittingContextMailBox getMailBox() {
        return mailBox;
    }

    public void setMailBox(EventCommittingContextMailBox mailBox) {
        this.mailBox = mailBox;
    }
}
