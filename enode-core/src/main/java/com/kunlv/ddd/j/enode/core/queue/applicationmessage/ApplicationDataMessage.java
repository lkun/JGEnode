package com.kunlv.ddd.j.enode.core.queue.applicationmessage;

/**
 * @author lvk618@gmail.com
 */
public class ApplicationDataMessage {
    private String applicationMessageData;
    private String applicationMessageType;

    public ApplicationDataMessage(String applicationMessageData, String applicationMessageType) {
        this.applicationMessageData = applicationMessageData;
        this.applicationMessageType = applicationMessageType;
    }

    public String getApplicationMessageData() {
        return applicationMessageData;
    }

    public void setApplicationMessageData(String applicationMessageData) {
        this.applicationMessageData = applicationMessageData;
    }

    public String getApplicationMessageType() {
        return applicationMessageType;
    }

    public void setApplicationMessageType(String applicationMessageType) {
        this.applicationMessageType = applicationMessageType;
    }
}
