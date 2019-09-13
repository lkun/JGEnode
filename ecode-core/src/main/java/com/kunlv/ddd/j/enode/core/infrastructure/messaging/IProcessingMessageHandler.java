package com.kunlv.ddd.j.enode.core.infrastructure.messaging;

public interface IProcessingMessageHandler<X extends IProcessingMessage<X,Y>, Y extends IMessage> {
    void handleAsync(X processingMessage);
}
