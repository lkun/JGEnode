package com.kunlv.ddd.j.enode.core.queue;

public interface IMessageContext {
    /**
     * 消息处理后执行
     *
     * @param message
     */
    void onMessageHandled(QueueMessage message);
}
