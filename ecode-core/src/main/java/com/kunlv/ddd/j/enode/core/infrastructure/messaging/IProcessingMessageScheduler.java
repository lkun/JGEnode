package com.kunlv.ddd.j.enode.core.infrastructure.messaging;

public interface IProcessingMessageScheduler<X extends IProcessingMessage<X, Y>, Y extends IMessage> {
    void scheduleMessage(X processingMessage);

    void scheduleMailbox(ProcessingMessageMailbox<X, Y> mailbox);
}
