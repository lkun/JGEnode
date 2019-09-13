package com.kunlv.ddd.j.enode.example.domain.bank.deposittransaction;

import com.kunlv.ddd.j.enode.core.eventing.DomainEvent;

/// <summary>存款交易已完成
/// </summary>
public class DepositTransactionCompletedEvent extends DomainEvent<String> {
    public String AccountId;

    public DepositTransactionCompletedEvent() {
    }

    public DepositTransactionCompletedEvent(String accountId) {
        AccountId = accountId;
    }
}
