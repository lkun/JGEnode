package com.kunlv.ddd.j.enode.core.commanding;

import com.kunlv.ddd.j.enode.core.infrastructure.IObjectProxy;
import com.kunlv.ddd.j.enode.core.infrastructure.MethodInvocation;

public interface ICommandHandlerProxy extends IObjectProxy, MethodInvocation{
    void handle(ICommandContext context, ICommand command);
}
