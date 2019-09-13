package com.kunlv.ddd.j.enode.core.messaging.impl;

import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.core.messaging.IMessage;
import com.kunlv.ddd.j.enode.core.messaging.IMessagePublisher;

import java.util.concurrent.CompletableFuture;

/**
 * @author lvk618@gmail.com
 */
public class DoNothingPublisher<TMessage extends IMessage> implements IMessagePublisher<TMessage> {
    private static final CompletableFuture<AsyncTaskResult> SUCCESSRESULTTASK = CompletableFuture.completedFuture(AsyncTaskResult.Success);

    @Override
    public CompletableFuture<AsyncTaskResult> publishAsync(TMessage message) {
        return SUCCESSRESULTTASK;
    }
}
