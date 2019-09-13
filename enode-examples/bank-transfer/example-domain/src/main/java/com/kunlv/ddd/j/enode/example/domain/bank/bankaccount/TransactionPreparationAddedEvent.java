package com.kunlv.ddd.j.enode.example.domain.bank.bankaccount;

import com.kunlv.ddd.j.enode.core.eventing.DomainEvent;

/// <summary>账户预操作已添加
/// </summary>
public class TransactionPreparationAddedEvent extends DomainEvent<String> {
    public TransactionPreparation TransactionPreparation;

    public TransactionPreparationAddedEvent() {
    }

    public TransactionPreparationAddedEvent(TransactionPreparation transactionPreparation) {
        TransactionPreparation = transactionPreparation;
    }
}
