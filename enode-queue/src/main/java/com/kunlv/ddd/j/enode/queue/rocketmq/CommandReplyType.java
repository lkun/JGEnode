package com.kunlv.ddd.j.enode.queue.rocketmq;

public enum CommandReplyType {
    Other((short)0),//保持与C#状态一致
    CommandExecuted((short)1),
    DomainEventHandled((short)2);

    short value;

    CommandReplyType(short value){
        this.value=value;
    }

    public short getValue(){
        return value;
    }
}
