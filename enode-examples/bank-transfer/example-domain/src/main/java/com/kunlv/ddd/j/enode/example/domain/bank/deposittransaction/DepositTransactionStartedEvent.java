package com.kunlv.ddd.j.enode.example.domain.bank.deposittransaction;

import com.kunlv.ddd.j.enode.core.eventing.DomainEvent;

public class DepositTransactionStartedEvent extends DomainEvent<String> {
    public String AccountId;
    public double Amount;

    public DepositTransactionStartedEvent() {
    }

    public DepositTransactionStartedEvent(String accountId, double amount) {
        AccountId = accountId;
        Amount = amount;
    }
}
