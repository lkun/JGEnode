package com.kunlv.ddd.j.enode.core.commanding;

import com.kunlv.ddd.j.enode.core.messaging.MessageHandlerData;

import java.util.List;

public interface ICommandAsyncHandlerProvider {
    /**
     * Get all the async handlers for the given command type.
     *
     * @param commandType
     * @return
     */
    List<MessageHandlerData<ICommandAsyncHandlerProxy>> getHandlers(Class commandType);
}
