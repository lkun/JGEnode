package com.kunlv.ddd.j.enode.example.commands.bank;

import com.kunlv.ddd.j.enode.core.commanding.Command;

/// <summary>确认预存款
/// </summary>
public class ConfirmDepositPreparationCommand extends Command {
    public ConfirmDepositPreparationCommand() {
    }

    public ConfirmDepositPreparationCommand(String transactionId) {
        super(transactionId);
    }
}
