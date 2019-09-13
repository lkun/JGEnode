package com.kunlv.ddd.j.enode.example.commands.bank;

import com.kunlv.ddd.j.enode.core.commanding.Command;

/// <summary>向账户添加一笔预操作
/// </summary>
public class AddTransactionPreparationCommand extends Command {
    public String TransactionId;
    public int TransactionType;
    public int PreparationType;
    public double Amount;

    public AddTransactionPreparationCommand() {
    }

    public AddTransactionPreparationCommand(String accountId, String transactionId, int transactionType, int preparationType, double amount) {
        super(accountId);
        TransactionId = transactionId;
        TransactionType = transactionType;
        PreparationType = preparationType;
        Amount = amount;
    }
}
