package com.kunlv.ddd.j.enode.core.commanding.impl;

import com.kunlv.ddd.j.enode.core.commanding.ICommand;
import com.kunlv.ddd.j.enode.core.commanding.ICommandAsyncHandler;
import com.kunlv.ddd.j.enode.core.commanding.ICommandAsyncHandlerProxy;
import com.kunlv.ddd.j.enode.common.container.IObjectContainer;
import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.core.infrastructure.Handled;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IApplicationMessage;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

public class CommandAsyncHandlerProxy implements ICommandAsyncHandlerProxy {
    private IObjectContainer _objectContainer;
    private Class _commandHandlerType;
    private ICommandAsyncHandler _commandHandler;
    private MethodHandle _methodHandle;
    private Method _method;
    private boolean _isCheckCommandHandledFirst;

    public CommandAsyncHandlerProxy(IObjectContainer objectContainer, Class commandHandlerType, ICommandAsyncHandler commandHandler, MethodHandle methodHandle, Method method) {
        _objectContainer = objectContainer;
        _commandHandlerType = commandHandlerType;
        _commandHandler = commandHandler;
        _methodHandle = methodHandle;
        _method = method;
        _isCheckCommandHandledFirst = parseCheckCommandHandledFirst();
    }

    @Override
    public CompletableFuture<AsyncTaskResult<IApplicationMessage>> handleAsync(ICommand command) {
        ICommandAsyncHandler handler = (ICommandAsyncHandler) getInnerObject();
        try {
            return (CompletableFuture<AsyncTaskResult<IApplicationMessage>>) _methodHandle.invoke(handler, command);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean checkCommandHandledFirst() {
        return _isCheckCommandHandledFirst;
    }

    @Override
    public Object getInnerObject() {
        if (_commandHandler != null)
            return _commandHandler;

        return _objectContainer.resolve(_commandHandlerType);
    }

    private boolean parseCheckCommandHandledFirst() {
        Handled handled = _method.getAnnotation(Handled.class);

        if (handled != null)
            return handled.value();

        handled = _commandHandler.getClass().getAnnotation(Handled.class);

        if (handled != null)
            return handled.value();

        //default handled first
        return true;
    }

    @Override
    public Method getMethod() {
        return _method;
    }
}
