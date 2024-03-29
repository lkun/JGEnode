package com.kunlv.ddd.j.enode.core.infrastructure.messaging.impl;

import  com.kunlv.ddd.j.enode.common.container.IObjectContainer;
import  com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessage;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessageHandler;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessageHandlerProxy3;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MessageHandlerProxy3 implements IMessageHandlerProxy3 {
    private IObjectContainer _objectContainer;
    private Class _handlerType;
    private IMessageHandler _handler;
    private MethodHandle _methodHandle;
    private Method _method;
    private Class<?>[] _methodParameterTypes;

    public MessageHandlerProxy3(IObjectContainer objectContainer, Class handlerType, IMessageHandler handler, MethodHandle methodHandle, Method method) {
        _objectContainer = objectContainer;
        _handlerType = handlerType;
        _handler = handler;
        _methodHandle = methodHandle;
        _method = method;
        _methodParameterTypes = method.getParameterTypes();
    }

    @Override
    public CompletableFuture<AsyncTaskResult> handleAsync(IMessage message1, IMessage message2, IMessage message3) {
        IMessageHandler handler = (IMessageHandler) getInnerObject();

        List<Class<?>> parameterTypes = Arrays.asList(_methodParameterTypes);

        List<IMessage> params = new ArrayList<>();
        params.add(message1);
        params.add(message2);
        params.add(message3);

        //排序参数
        params.sort((m1, m2) ->
                getMessageParameterIndex(parameterTypes, m1) - getMessageParameterIndex(parameterTypes, m2)
        );

        try {
            //参数按照方法定义参数类型列表传递
            return (CompletableFuture<AsyncTaskResult>) _methodHandle.invoke(handler, params.get(0), params.get(1), params.get(2));
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    private int getMessageParameterIndex(List<Class<?>> methodParameterTypes, IMessage message) {
        int i = 0;
        for (Class paramType : methodParameterTypes) {
            if (paramType.isAssignableFrom(message.getClass())) {
                return i;
            }
            i++;
        }

        return i;
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
