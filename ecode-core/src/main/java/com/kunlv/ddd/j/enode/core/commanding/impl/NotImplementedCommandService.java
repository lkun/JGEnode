package com.kunlv.ddd.j.enode.core.commanding.impl;

import com.kunlv.ddd.j.enode.core.commanding.CommandResult;
import com.kunlv.ddd.j.enode.core.commanding.CommandReturnType;
import com.kunlv.ddd.j.enode.core.commanding.ICommand;
import com.kunlv.ddd.j.enode.core.commanding.ICommandService;
import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;

import java.util.concurrent.CompletableFuture;

public class NotImplementedCommandService implements ICommandService {
    @Override
    public CompletableFuture<AsyncTaskResult> sendAsync(ICommand command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<AsyncTaskResult> sendAsyncAll(ICommand... commands) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void send(ICommand command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CommandResult execute(ICommand command, int timeoutMillis) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CommandResult execute(ICommand command, CommandReturnType commandReturnType, int timeoutMillis) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<AsyncTaskResult<CommandResult>> executeAsync(ICommand command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<AsyncTaskResult<CommandResult>> executeAsync(ICommand command, CommandReturnType commandReturnType) {
        throw new UnsupportedOperationException();
    }
}
