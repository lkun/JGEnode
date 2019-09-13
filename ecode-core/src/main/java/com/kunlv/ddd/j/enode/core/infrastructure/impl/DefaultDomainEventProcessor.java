package com.kunlv.ddd.j.enode.core.infrastructure.impl;

import  com.kunlv.ddd.j.enode.common.scheduling.IScheduleService;
import  com.kunlv.ddd.j.enode.core.eventing.DomainEventStreamMessage;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IProcessingMessageHandler;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IProcessingMessageScheduler;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.ProcessingDomainEventStreamMessage;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.impl.DefaultMessageProcessor;

import javax.inject.Inject;

public class DefaultDomainEventProcessor extends DefaultMessageProcessor<ProcessingDomainEventStreamMessage, DomainEventStreamMessage> {
    @Inject
    public DefaultDomainEventProcessor(
            IProcessingMessageScheduler<ProcessingDomainEventStreamMessage, DomainEventStreamMessage> processingMessageScheduler,
            IProcessingMessageHandler<ProcessingDomainEventStreamMessage, DomainEventStreamMessage> processingMessageHandler,
            IScheduleService scheduleService) {
        super(processingMessageScheduler, processingMessageHandler, scheduleService);
    }

    @Override
    public String getMessageName() {
        return "event message";
    }
}
