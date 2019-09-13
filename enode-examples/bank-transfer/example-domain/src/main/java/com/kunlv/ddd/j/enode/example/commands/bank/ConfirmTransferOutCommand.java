package com.kunlv.ddd.j.enode.example.commands.bank;

import com.kunlv.ddd.j.enode.core.commanding.Command;

/// <summary>确认转出
/// </summary>
public class ConfirmTransferOutCommand extends Command {
    public ConfirmTransferOutCommand() {
    }

    public ConfirmTransferOutCommand(String transactionId) {
        super(transactionId);
    }
}
