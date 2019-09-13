package com.kunlv.ddd.j.enode.core.publishableexception;

import com.kunlv.ddd.j.enode.core.messaging.IEventProcessContext;

/**
 * @author lvk618@gmail.com
 */
public class ProcessingPublishableExceptionMessage {
    private IEventProcessContext processContext;
    private IPublishableException message;

    public ProcessingPublishableExceptionMessage(IPublishableException message, IEventProcessContext processContext) {
        this.message = message;
        this.processContext = processContext;
    }

    public void complete() {
        processContext.notifyEventProcessed();
    }

    public IPublishableException getMessage() {
        return message;
    }
}
