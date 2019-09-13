package com.kunlv.ddd.j.enode.core.infrastructure.impl;

import  com.kunlv.ddd.j.enode.common.scheduling.IScheduleService;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IProcessingMessageHandler;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IProcessingMessageScheduler;
import  com.kunlv.ddd.j.enode.core.infrastructure.IPublishableException;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.ProcessingPublishableExceptionMessage;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.impl.DefaultMessageProcessor;

import javax.inject.Inject;

public class DefaultPublishableExceptionProcessor extends DefaultMessageProcessor<ProcessingPublishableExceptionMessage, IPublishableException> {
    @Inject
    public DefaultPublishableExceptionProcessor(
            IProcessingMessageScheduler<ProcessingPublishableExceptionMessage, IPublishableException> processingMessageScheduler,
            IProcessingMessageHandler<ProcessingPublishableExceptionMessage, IPublishableException> processingMessageHandler,
            IScheduleService scheduleService) {
        super(processingMessageScheduler, processingMessageHandler, scheduleService);
    }

    @Override
    public String getMessageName() {
        return "exception message";
    }
}

