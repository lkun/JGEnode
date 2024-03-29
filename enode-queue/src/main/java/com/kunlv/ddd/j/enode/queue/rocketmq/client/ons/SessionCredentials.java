package com.kunlv.ddd.j.enode.queue.rocketmq.client.ons;


import com.alibaba.rocketmq.common.MixAll;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Properties;

public class SessionCredentials {
    public static final Charset CHARSET = Charset.forName("UTF-8");
    public static final String AccessKey = "AccessKey";
    public static final String SecretKey = "SecretKey";
    public static final String Signature = "Signature";
    public static final String SignatureMethod = "SignatureMethod";
    public static final String ONSChannelKey = "OnsChannel";

    public static final String KeyFile = System.getProperty("rocketmq.client.keyFile",
            System.getProperty("user.home") + File.separator + "onskey");

    private String accessKey;
    private String secretKey;
    private String signature;
    private String signatureMethod;
    private ONSChannel onsChannel = ONSChannel.ALIYUN;


    public SessionCredentials() {
        final String keyContent = MixAll.file2String(KeyFile);
        if (keyContent != null) {
            Properties prop = MixAll.string2Properties(keyContent);
            if (prop != null) {
                this.updateContent(prop);
            }
        }
    }


    public void updateContent(Properties prop) {
        {
            String value = prop.getProperty(AccessKey);
            if (value != null) {
                this.accessKey = value.trim();
            }
        }
        {
            String value = prop.getProperty(SecretKey);
            if (value != null) {
                this.secretKey = value.trim();
            }
        }
        {
            Object value = prop.get(ONSChannelKey);
            if (value != null) {
                this.onsChannel = ONSChannel.valueOf(value.toString());
            }
        }
    }


    public String getAccessKey() {
        return accessKey;
    }


    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }


    public String getSecretKey() {
        return secretKey;
    }


    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }


    public String getSignature() {
        return signature;
    }


    public void setSignature(String signature) {
        this.signature = signature;
    }


    public String getSignatureMethod() {
        return signatureMethod;
    }


    public void setSignatureMethod(String signatureMethod) {
        this.signatureMethod = signatureMethod;
    }


    public ONSChannel getOnsChannel() {
        return onsChannel;
    }


    public void setOnsChannel(ONSChannel onsChannel) {
        this.onsChannel = onsChannel;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accessKey == null) ? 0 : accessKey.hashCode());
        result = prime * result + ((secretKey == null) ? 0 : secretKey.hashCode());
        result = prime * result + ((signature == null) ? 0 : signature.hashCode());
        result = prime * result + ((signatureMethod == null) ? 0 : signatureMethod.hashCode());
        result = prime * result + ((onsChannel == null) ? 0 : onsChannel.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        SessionCredentials other = (SessionCredentials) obj;
        if (accessKey == null) {
            if (other.accessKey != null)
                return false;
        } else if (!accessKey.equals(other.accessKey))
            return false;

        if (secretKey == null) {
            if (other.secretKey != null)
                return false;
        } else if (!secretKey.equals(other.secretKey))
            return false;

        if (signature == null) {
            if (other.signature != null)
                return false;
        } else if (!signature.equals(other.signature))
            return false;

        if (signatureMethod == null) {
            if (other.signatureMethod != null)
                return false;
        } else if (!signatureMethod.equals(other.signatureMethod))
            return false;

        if (onsChannel == null) {
            if (other.onsChannel != null)
                return false;
        } else if (!onsChannel.equals(other.onsChannel))
            return false;

        return true;
    }


    @Override
    public String toString() {
        return "SessionCredentials [accessKey=" + accessKey + ", secretKey=" + secretKey + ", signature="
                + signature + ", signatureMethod=" + signatureMethod + ", onsChannel=" + onsChannel + "]";
    }
}