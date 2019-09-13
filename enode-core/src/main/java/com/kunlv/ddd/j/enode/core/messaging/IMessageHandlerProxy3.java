package com.kunlv.ddd.j.enode.core.messaging;

import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.core.infrastructure.IObjectProxy;
import com.kunlv.ddd.j.enode.core.infrastructure.MethodInvocation;

import java.util.concurrent.CompletableFuture;

public interface IMessageHandlerProxy3 extends IObjectProxy, MethodInvocation {
    CompletableFuture<AsyncTaskResult> handleAsync(IMessage message1, IMessage message2, IMessage message3);
}
