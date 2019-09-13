package com.kunlv.ddd.j.enode.core.commanding;

import java.util.concurrent.CompletableFuture;

public interface IProcessingCommandHandler {
    CompletableFuture<Void> handleAsync(ProcessingCommand processingCommand);
}
