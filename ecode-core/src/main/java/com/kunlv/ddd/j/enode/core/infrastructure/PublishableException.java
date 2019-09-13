package com.kunlv.ddd.j.enode.core.infrastructure;

import com.kunlv.ddd.j.enode.common.utilities.ObjectId;

import java.util.Date;
import java.util.Map;

public abstract class PublishableException extends RuntimeException implements IPublishableException {
    private static final long serialVersionUID = 2099914413380872726L;

    private String id;
    private Date timestamp;
    private int sequence;

    public PublishableException() {
        id = ObjectId.generateNewStringId();
        timestamp = new Date();
        sequence = 1;
    }

    public abstract void serializeTo(Map<String, String> serializableInfo);

    public abstract void restoreFrom(Map<String, String> serializableInfo);

    public String getRoutingKey() {
        return null;
    }

    public String getTypeName() {
        return this.getClass().getName();
    }

    public String id() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date timestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int sequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
