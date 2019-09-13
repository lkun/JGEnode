package com.kunlv.ddd.j.enode.example.domain.bank.bankaccount;

import com.kunlv.ddd.j.enode.core.eventing.DomainEvent;

/// <summary>已开户
/// </summary>
public class AccountCreatedEvent extends DomainEvent<String> {
    /// <summary>账户拥有者
    /// </summary>
    public String Owner;

    public AccountCreatedEvent() {
    }

    public AccountCreatedEvent(String owner) {
        Owner = owner;
    }
}
