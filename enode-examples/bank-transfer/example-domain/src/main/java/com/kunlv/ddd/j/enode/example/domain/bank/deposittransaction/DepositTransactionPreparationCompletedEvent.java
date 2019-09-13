package com.kunlv.ddd.j.enode.example.domain.bank.deposittransaction;

import com.kunlv.ddd.j.enode.core.eventing.DomainEvent;

/// <summary>存款交易预存款已确认
/// </summary>
public class DepositTransactionPreparationCompletedEvent extends DomainEvent<String> {
    public String AccountId;

    public DepositTransactionPreparationCompletedEvent() {
    }

    public DepositTransactionPreparationCompletedEvent(String accountId) {
        AccountId = accountId;
    }
}
