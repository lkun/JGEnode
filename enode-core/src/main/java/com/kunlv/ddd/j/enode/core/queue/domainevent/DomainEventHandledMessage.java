package com.kunlv.ddd.j.enode.core.queue.domainevent;

/**
 * @author lvk618@gmail.com
 */
public class DomainEventHandledMessage {
    private String commandId;
    private String aggregateRootId;
    private String commandResult;

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public String getAggregateRootId() {
        return aggregateRootId;
    }

    public void setAggregateRootId(String aggregateRootId) {
        this.aggregateRootId = aggregateRootId;
    }

    public String getCommandResult() {
        return commandResult;
    }

    public void setCommandResult(String commandResult) {
        this.commandResult = commandResult;
    }

    @Override
    public String toString() {
        return "DomainEventHandledMessage{" +
                "commandId='" + commandId + '\'' +
                ", aggregateRootId='" + aggregateRootId + '\'' +
                ", commandResult='" + commandResult + '\'' +
                '}';
    }
}