package com.kunlv.ddd.j.enode.core.commanding;

import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IApplicationMessage;
import com.kunlv.ddd.j.enode.core.infrastructure.IObjectProxy;
import com.kunlv.ddd.j.enode.core.infrastructure.MethodInvocation;

import java.util.concurrent.CompletableFuture;

public interface ICommandAsyncHandlerProxy extends IObjectProxy, MethodInvocation {
    CompletableFuture<AsyncTaskResult<IApplicationMessage>> handleAsync(ICommand command);

    boolean checkCommandHandledFirst();
}
