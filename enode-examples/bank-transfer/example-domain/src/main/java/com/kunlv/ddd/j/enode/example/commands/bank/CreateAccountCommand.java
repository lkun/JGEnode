package com.kunlv.ddd.j.enode.example.commands.bank;

import com.kunlv.ddd.j.enode.core.commanding.Command;

public class CreateAccountCommand extends Command {
    public String Owner;

    public CreateAccountCommand() {
    }

    public CreateAccountCommand(String accountId, String owner) {
        super(accountId);
        Owner = owner;
    }
}
