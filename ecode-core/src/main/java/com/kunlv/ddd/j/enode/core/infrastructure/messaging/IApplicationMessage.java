package com.kunlv.ddd.j.enode.core.infrastructure.messaging;

public interface IApplicationMessage extends IMessage {
    long getStartDeliverTime();
    void setStartDeliverTime(long startDeliverTime);
}
