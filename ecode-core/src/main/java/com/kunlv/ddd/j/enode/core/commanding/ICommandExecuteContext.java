package com.kunlv.ddd.j.enode.core.commanding;

public interface ICommandExecuteContext extends ICommandContext, ITrackingContext {
    void onCommandExecuted(CommandResult commandResult);
}
