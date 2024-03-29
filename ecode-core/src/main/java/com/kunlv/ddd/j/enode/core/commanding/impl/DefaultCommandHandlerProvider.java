package com.kunlv.ddd.j.enode.core.commanding.impl;

import com.kunlv.ddd.j.enode.core.commanding.*;
import com.kunlv.ddd.j.enode.common.container.IObjectContainer;
import com.kunlv.ddd.j.enode.core.infrastructure.impl.AbstractHandlerProvider;

import javax.inject.Inject;
import java.lang.reflect.Method;

public class DefaultCommandHandlerProvider extends AbstractHandlerProvider<Class, ICommandHandlerProxy, Class> implements ICommandHandlerProvider {
    private IObjectContainer objectContainer;

    @Inject
    public DefaultCommandHandlerProvider(IObjectContainer objectContainer) {
        this.objectContainer = objectContainer;
    }

    public DefaultCommandHandlerProvider(){
    }
    protected Class getHandlerType() {
        return ICommandHandler.class;
    }

    protected Class getKey(Method method) {
        return method.getParameterTypes()[1];
    }

    protected Class getHandlerProxyImplementationType() {
        return CommandHandlerProxy.class;
    }

    protected boolean isHandlerSourceMatchKey(Class handlerSource, Class key) {
        return key.isAssignableFrom(handlerSource);
    }

    protected boolean isHandleMethodMatchKey(Class[] argumentTypes, Class key) {
        return argumentTypes.length == 1 && argumentTypes[0] == key;
    }

    protected boolean isHandleMethodMatch(Method method) {
        return method.getName().equals("handle")
                && method.getParameterTypes().length == 2
                && method.getParameterTypes()[0] == ICommandContext.class
                && ICommand.class.isAssignableFrom(method.getParameterTypes()[1]);
    }

    @Override
    protected IObjectContainer getObjectContainer() {
        return objectContainer;
    }
}
