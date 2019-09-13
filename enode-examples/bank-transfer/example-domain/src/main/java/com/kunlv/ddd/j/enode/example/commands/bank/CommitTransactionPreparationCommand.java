package com.kunlv.ddd.j.enode.example.commands.bank;

import com.kunlv.ddd.j.enode.core.commanding.Command;

/// <summary>提交预操作
/// </summary>
public class CommitTransactionPreparationCommand extends Command {
    public String TransactionId;

    public CommitTransactionPreparationCommand() {
    }

    public CommitTransactionPreparationCommand(String accountId, String transactionId) {
        super(accountId);
        TransactionId = transactionId;
    }
}
