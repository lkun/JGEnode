package com.kunlv.ddd.j.enode.core.infrastructure.messaging;

import java.util.List;

public interface IMessageHandlerProvider {
    List<MessageHandlerData<IMessageHandlerProxy1>> getHandlers(Class messageType);
}
