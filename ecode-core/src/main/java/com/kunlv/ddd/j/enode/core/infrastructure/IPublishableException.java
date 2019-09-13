package com.kunlv.ddd.j.enode.core.infrastructure;

import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessage;

import java.util.Map;

public interface IPublishableException extends IMessage {
    void serializeTo(Map<String, String> serializableInfo);

    void restoreFrom(Map<String, String> serializableInfo);
}
