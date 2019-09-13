package com.kunlv.ddd.j.enode.core.commanding.impl;

import com.kunlv.ddd.j.enode.core.commanding.ICommand;
import com.kunlv.ddd.j.enode.core.commanding.ICommandContext;
import com.kunlv.ddd.j.enode.core.commanding.ICommandHandler;
import com.kunlv.ddd.j.enode.core.commanding.ICommandHandlerProxy;
import com.kunlv.ddd.j.enode.common.container.IObjectContainer;
import com.kunlv.ddd.j.enode.core.infrastructure.WrappedRuntimeException;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

public class CommandHandlerProxy implements ICommandHandlerProxy {
    private IObjectContainer _objectContainer;
    private Class _commandHandlerType;
    private ICommandHandler _commandHandler;
    private MethodHandle _methodHandle;
    private Method _method;

    public CommandHandlerProxy(IObjectContainer objectContainer, Class commandHandlerType, ICommandHandler commandHandler, MethodHandle methodHandle, Method method) {
        _objectContainer = objectContainer;
        _commandHandlerType = commandHandlerType;
        _commandHandler = commandHandler;
        _methodHandle = methodHandle;
        _method = method;
    }

    @Override
    public void handle(ICommandContext context, ICommand command) {
        ICommandHandler handler = (ICommandHandler) getInnerObject();
        try {
            _methodHandle.invoke(handler, context, command);
        } catch (Exception e) {
            throw new WrappedRuntimeException(e);
        } catch (Throwable throwable) {
            throw new WrappedRuntimeException(new RuntimeException(throwable));
        }
    }

    @Override
    public Object getInnerObject() {
        if (_commandHandler != null)
            return _commandHandler;

        return _objectContainer.resolve(_commandHandlerType);
    }

    @Override
    public Method getMethod() {
        return _method;
    }
}
