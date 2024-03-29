package com.kunlv.ddd.j.enode.core.infrastructure.messaging;

public interface IProcessingMessage<X extends IProcessingMessage<X,Y>, Y extends IMessage> {
    Y getMessage();
    void setMailbox(ProcessingMessageMailbox<X, Y> mailbox);
    void complete();
}
