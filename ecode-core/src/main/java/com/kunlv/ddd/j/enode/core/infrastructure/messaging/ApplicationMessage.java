package com.kunlv.ddd.j.enode.core.infrastructure.messaging;

import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IApplicationMessage;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.Message;

public abstract class ApplicationMessage extends Message implements IApplicationMessage {
    private long startDeliverTime;
    @Override
    public long getStartDeliverTime() {
        return startDeliverTime;
    }

    @Override
    public void setStartDeliverTime(long startDeliverTime) {
        this.startDeliverTime = startDeliverTime;
    }
}
