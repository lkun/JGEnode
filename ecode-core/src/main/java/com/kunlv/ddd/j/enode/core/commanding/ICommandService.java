package com.kunlv.ddd.j.enode.core.commanding;


import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;

import java.util.concurrent.CompletableFuture;

public interface ICommandService {

    CompletableFuture<AsyncTaskResult> sendAsync(ICommand command);

    CompletableFuture<AsyncTaskResult> sendAsyncAll(ICommand... commands);

    void send(ICommand command);

    CommandResult execute(ICommand command, int timeoutMillis);

    CommandResult execute(ICommand command, CommandReturnType commandReturnType, int timeoutMillis);

    CompletableFuture<AsyncTaskResult<CommandResult>> executeAsync(ICommand command);

    CompletableFuture<AsyncTaskResult<CommandResult>> executeAsync(ICommand command, CommandReturnType commandReturnType);
}
