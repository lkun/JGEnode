package com.kunlv.ddd.j.enode.core.applicationmessage;

import com.kunlv.ddd.j.enode.core.messaging.IEventProcessContext;

/**
 * @author lvk618@gmail.com
 */
public class ProcessingApplicationMessage {
    public IApplicationMessage message;
    private IEventProcessContext processContext;

    public ProcessingApplicationMessage(IApplicationMessage message, IEventProcessContext processContext) {
        this.message = message;
        this.processContext = processContext;
    }

    public void complete() {
        processContext.notifyEventProcessed();
    }

    public IApplicationMessage getMessage() {
        return message;
    }
}
