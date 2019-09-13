package com.kunlv.ddd.j.enode.core.messaging;

import java.util.List;

public interface IThreeMessageHandlerProvider {
    List<MessageHandlerData<IMessageHandlerProxy3>> getHandlers(List<Class> messageTypes);
}
