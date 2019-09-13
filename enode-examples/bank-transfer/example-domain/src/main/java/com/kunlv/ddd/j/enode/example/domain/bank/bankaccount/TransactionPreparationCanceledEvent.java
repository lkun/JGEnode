package com.kunlv.ddd.j.enode.example.domain.bank.bankaccount;

import com.kunlv.ddd.j.enode.core.eventing.DomainEvent;

/// <summary>账户预操作已取消
/// </summary>
public class TransactionPreparationCanceledEvent extends DomainEvent<String> {
    public TransactionPreparation TransactionPreparation;

    public TransactionPreparationCanceledEvent() {
    }

    public TransactionPreparationCanceledEvent(TransactionPreparation transactionPreparation) {
        TransactionPreparation = transactionPreparation;
    }
}
