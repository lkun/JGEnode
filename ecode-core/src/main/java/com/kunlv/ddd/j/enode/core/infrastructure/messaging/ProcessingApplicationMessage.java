package com.kunlv.ddd.j.enode.core.infrastructure.messaging;

public class ProcessingApplicationMessage implements IProcessingMessage<ProcessingApplicationMessage, IApplicationMessage> {
    private ProcessingMessageMailbox<ProcessingApplicationMessage, IApplicationMessage> _mailbox;
    private IMessageProcessContext _processContext;

    public IApplicationMessage message;

    public ProcessingApplicationMessage(IApplicationMessage message, IMessageProcessContext processContext) {
        this.message = message;
        _processContext = processContext;
    }

    public void setMailbox(ProcessingMessageMailbox<ProcessingApplicationMessage, IApplicationMessage> mailbox) {
        _mailbox = mailbox;
    }

    public void complete() {
        _processContext.notifyMessageProcessed();
        if (_mailbox != null) {
            _mailbox.completeMessage(this);
        }
    }

    @Override
    public IApplicationMessage getMessage() {
        return message;
    }
}
