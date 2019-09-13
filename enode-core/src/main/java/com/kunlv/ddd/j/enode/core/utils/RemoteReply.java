package com.kunlv.ddd.j.enode.core.utils;

import com.kunlv.ddd.j.enode.core.commanding.CommandResult;
import com.kunlv.ddd.j.enode.core.queue.domainevent.DomainEventHandledMessage;

/**
 * remote reply used by vert.x
 *
 * @author lvk618@gmail.com
 */
public class RemoteReply {
    private int code;
    private CommandResult commandResult;
    private DomainEventHandledMessage eventHandledMessage;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public CommandResult getCommandResult() {
        return commandResult;
    }

    public void setCommandResult(CommandResult commandResult) {
        this.commandResult = commandResult;
    }

    public DomainEventHandledMessage getEventHandledMessage() {
        return eventHandledMessage;
    }

    public void setEventHandledMessage(DomainEventHandledMessage eventHandledMessage) {
        this.eventHandledMessage = eventHandledMessage;
    }

    @Override
    public String toString() {
        return "RemoteReply{" +
                "code=" + code +
                ", commandResult=" + commandResult +
                ", eventHandledMessage=" + eventHandledMessage +
                '}';
    }
}
