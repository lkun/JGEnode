package com.kunlv.ddd.j.enode.core.infrastructure.messaging.impl;

import  com.kunlv.ddd.j.enode.common.container.IObjectContainer;
import com.kunlv.ddd.j.enode.core.infrastructure.impl.AbstractHandlerProvider;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessage;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessageHandler;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessageHandlerProvider;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessageHandlerProxy1;

import javax.inject.Inject;
import java.lang.reflect.Method;

public class DefaultMessageHandlerProvider extends AbstractHandlerProvider<Class, IMessageHandlerProxy1, Class> implements IMessageHandlerProvider {
    private IObjectContainer objectContainer;

    @Inject
    public DefaultMessageHandlerProvider(IObjectContainer objectContainer) {
        this.objectContainer = objectContainer;
    }

    @Override
    protected Class getHandlerType() {
        return IMessageHandler.class;
    }

    @Override
    protected Class getKey(Method method) {
        return method.getParameterTypes()[0];
    }

    @Override
    protected Class<? extends IMessageHandlerProxy1> getHandlerProxyImplementationType() {
        return MessageHandlerProxy1.class;
    }

    @Override
    protected boolean isHandlerSourceMatchKey(Class handlerSource, Class key) {
        return key.isAssignableFrom(handlerSource);
    }

    @Override
    protected boolean isHandleMethodMatch(Method method) {
        return method.getName().equals("handleAsync")
                && method.getParameterTypes().length == 1
                && IMessage.class.isAssignableFrom(method.getParameterTypes()[0]);
    }

    @Override
    protected IObjectContainer getObjectContainer() {
        return objectContainer;
    }
}
