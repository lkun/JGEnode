package com.kunlv.ddd.j.enode.example.domain.bank.bankaccount;

import com.kunlv.ddd.j.enode.core.eventing.DomainEvent;

/// <summary>账户预操作已执行
/// </summary>
public class TransactionPreparationCommittedEvent extends DomainEvent<String> {
    public double CurrentBalance;
    public TransactionPreparation TransactionPreparation;

    public TransactionPreparationCommittedEvent() {
    }

    public TransactionPreparationCommittedEvent(double currentBalance, TransactionPreparation transactionPreparation) {
        CurrentBalance = currentBalance;
        TransactionPreparation = transactionPreparation;
    }
}
