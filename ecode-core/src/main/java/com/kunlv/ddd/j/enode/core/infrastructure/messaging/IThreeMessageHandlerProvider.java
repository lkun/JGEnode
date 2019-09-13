package com.kunlv.ddd.j.enode.core.infrastructure.messaging;

import java.util.List;

public interface IThreeMessageHandlerProvider {
    List<MessageHandlerData<IMessageHandlerProxy3>> getHandlers(List<Class> messageTypes);
}
