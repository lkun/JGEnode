package com.kunlv.ddd.j.enode.core.infrastructure.messaging.impl;

import  com.kunlv.ddd.j.enode.common.scheduling.IScheduleService;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IApplicationMessage;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IProcessingMessageHandler;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IProcessingMessageScheduler;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.ProcessingApplicationMessage;

import javax.inject.Inject;

public class DefaultApplicationMessageProcessor extends DefaultMessageProcessor<ProcessingApplicationMessage, IApplicationMessage> {

    @Inject
    public DefaultApplicationMessageProcessor(
            IProcessingMessageScheduler<ProcessingApplicationMessage, IApplicationMessage> processingMessageScheduler,
            IProcessingMessageHandler<ProcessingApplicationMessage, IApplicationMessage> processingMessageHandler,
            IScheduleService scheduleService) {
        super(processingMessageScheduler, processingMessageHandler, scheduleService);
    }

    @Override
    public String getMessageName() {
        return "application message";
    }
}
