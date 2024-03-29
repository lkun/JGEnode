package com.kunlv.ddd.j.enode.core.commanding.impl;

import com.kunlv.ddd.j.enode.core.commanding.*;
import com.kunlv.ddd.j.enode.common.container.IObjectContainer;
import com.kunlv.ddd.j.enode.core.infrastructure.impl.AbstractHandlerProvider;

import javax.inject.Inject;
import java.lang.reflect.Method;

public class DefaultCommandAsyncHandlerProvider extends AbstractHandlerProvider<Class, ICommandAsyncHandlerProxy, Class> implements ICommandAsyncHandlerProvider {
    private IObjectContainer objectContainer;

    @Inject
    public DefaultCommandAsyncHandlerProvider(IObjectContainer objectContainer) {
        this.objectContainer = objectContainer;
    }

    @Override
    protected Class getHandlerType() {
        return ICommandAsyncHandler.class;
    }

    @Override
    protected Class getKey(Method method) {
        return method.getParameterTypes()[0];
    }

    @Override
    protected Class<? extends ICommandAsyncHandlerProxy> getHandlerProxyImplementationType() {
        return CommandAsyncHandlerProxy.class;
    }

    @Override
    protected boolean isHandlerSourceMatchKey(Class handlerSource, Class key) {
        return key.isAssignableFrom(handlerSource);
    }

    @Override
    protected boolean isHandleMethodMatch(Method method) {
        return method.getName().equals("handleAsync")
                && method.getParameterTypes().length == 1
                && ICommand.class.isAssignableFrom(method.getParameterTypes()[0]);
    }

    @Override
    protected IObjectContainer getObjectContainer() {
        return objectContainer;
    }
}
