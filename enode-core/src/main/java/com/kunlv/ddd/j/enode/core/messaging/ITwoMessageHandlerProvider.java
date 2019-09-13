package com.kunlv.ddd.j.enode.core.messaging;

import java.util.List;

public interface ITwoMessageHandlerProvider {
    List<MessageHandlerData<IMessageHandlerProxy2>> getHandlers(List<Class> messageTypes);
}
