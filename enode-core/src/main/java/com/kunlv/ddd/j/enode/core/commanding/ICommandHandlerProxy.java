package com.kunlv.ddd.j.enode.core.commanding;

import com.kunlv.ddd.j.enode.core.infrastructure.IObjectProxy;
import com.kunlv.ddd.j.enode.core.infrastructure.MethodInvocation;

import java.util.concurrent.CompletableFuture;

public interface ICommandHandlerProxy extends IObjectProxy, MethodInvocation {
    /**
     * Handle the given application command async. deal with aggregate in memory
     *
     * @param context
     * @param command
     * @return
     */
    CompletableFuture<Void> handleAsync(ICommandContext context, ICommand command);
}
