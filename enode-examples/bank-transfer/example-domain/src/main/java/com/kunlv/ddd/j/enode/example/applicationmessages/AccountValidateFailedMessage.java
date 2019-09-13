package com.kunlv.ddd.j.enode.example.applicationmessages;

import com.kunlv.ddd.j.enode.core.applicationmessage.ApplicationMessage;

/// <summary>账户验证未通过
/// </summary>
public class AccountValidateFailedMessage extends ApplicationMessage {
    public String AccountId;
    public String TransactionId;
    public String Reason;

    public AccountValidateFailedMessage() {
    }

    public AccountValidateFailedMessage(String accountId, String transactionId, String reason) {
        AccountId = accountId;
        TransactionId = transactionId;
        Reason = reason;
    }
}