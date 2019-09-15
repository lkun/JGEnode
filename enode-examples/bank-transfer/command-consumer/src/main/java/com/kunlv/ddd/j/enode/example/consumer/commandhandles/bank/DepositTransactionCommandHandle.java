package com.kunlv.ddd.j.enode.example.consumer.commandhandles.bank;

import com.kunlv.ddd.j.enode.common.io.Task;
import com.kunlv.ddd.j.enode.core.annotation.Command;
import com.kunlv.ddd.j.enode.core.annotation.Subscribe;
import com.kunlv.ddd.j.enode.core.commanding.ICommandContext;
import com.kunlv.ddd.j.enode.example.commands.bank.ConfirmDepositCommand;
import com.kunlv.ddd.j.enode.example.commands.bank.ConfirmDepositPreparationCommand;
import com.kunlv.ddd.j.enode.example.commands.bank.StartDepositTransactionCommand;
import com.kunlv.ddd.j.enode.example.domain.bank.deposittransaction.DepositTransaction;

import java.util.concurrent.CompletableFuture;

/**
 * 银行存款交易相关命令处理
 * <StartDepositTransactionCommand>,                      //开始交易
 * <ConfirmDepositPreparationCommand>,                    //确认预存款
 * <ConfirmDepositCommand>                                //确认存款
 */
@Command
public class DepositTransactionCommandHandle {
    @Subscribe
    public void handleAsync(ICommandContext context, StartDepositTransactionCommand command) {
        context.addAsync(new DepositTransaction(command.getAggregateRootId(), command.AccountId, command.Amount));
    }

    @Subscribe
    public void handleAsync(ICommandContext context, ConfirmDepositPreparationCommand command) {
        CompletableFuture<DepositTransaction> future = context.getAsync(command.getAggregateRootId(), DepositTransaction.class);
        DepositTransaction depositTransaction = Task.await(future);
        depositTransaction.ConfirmDepositPreparation();
    }

    @Subscribe
    public void handleAsync(ICommandContext context, ConfirmDepositCommand command) {
        CompletableFuture<DepositTransaction> future = context.getAsync(command.getAggregateRootId(), DepositTransaction.class);
        DepositTransaction depositTransaction = Task.await(future);
        depositTransaction.ConfirmDeposit();
    }
}