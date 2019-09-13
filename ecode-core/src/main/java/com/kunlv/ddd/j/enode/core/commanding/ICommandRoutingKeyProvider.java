package com.kunlv.ddd.j.enode.core.commanding;

public interface ICommandRoutingKeyProvider {
    String getRoutingKey(ICommand command);
}
