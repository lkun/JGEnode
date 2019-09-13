package com.kunlv.ddd.j.enode.core.infrastructure.messaging.impl;

import  com.kunlv.ddd.j.enode.common.container.IObjectContainer;
import  com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessage;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessageHandler;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessageHandlerProxy1;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

public class MessageHandlerProxy1 implements IMessageHandlerProxy1 {
    private IObjectContainer _objectContainer;
    private Class _handlerType;
    private IMessageHandler _handler;
    private MethodHandle _methodHandle;
    private Method _method;

    public MessageHandlerProxy1(IObjectContainer objectContainer, Class handlerType, IMessageHandler handler, MethodHandle methodHandle, Method method) {
        _objectContainer = objectContainer;
        _handlerType = handlerType;
        _handler = handler;
        _methodHandle = methodHandle;
        _method = method;
    }

    @Override
    public CompletableFuture<AsyncTaskResult> handleAsync(IMessage message) {
        IMessageHandler handler = (IMessageHandler) getInnerObject();
        try {
            return (CompletableFuture<AsyncTaskResult>) _methodHandle.invoke(handler, message);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    @Override
    public Object getInnerObject() {
        if (_handler != null)
            return _handler;

        return _objectContainer.resolve(_handlerType);
    }

    @Override
    public Method getMethod() {
        return _method;
    }
}
