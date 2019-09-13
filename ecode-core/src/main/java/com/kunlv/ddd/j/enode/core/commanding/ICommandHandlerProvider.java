package com.kunlv.ddd.j.enode.core.commanding;

import com.kunlv.ddd.j.enode.core.infrastructure.messaging.MessageHandlerData;

import java.util.List;

public interface ICommandHandlerProvider {
    List<MessageHandlerData<ICommandHandlerProxy>> getHandlers(Class commandType);
}
