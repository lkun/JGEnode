package com.kunlv.ddd.j.enode.core.command;

import com.kunlv.ddd.j.enode.core.commanding.ICommand;
import com.kunlv.ddd.j.enode.core.commanding.ICommandKeyProvider;

public class CommandKeyProvider implements ICommandKeyProvider {
    @Override
    public String getKey(ICommand command) {
//        return command.getAggregateRootId() == null ? command.id() : command.id() + MessageConst.KEY_SEPARATOR + command.getAggregateRootId();
        return command.getAggregateRootId() == null ? command.id() : command.getAggregateRootId();
    }
}
