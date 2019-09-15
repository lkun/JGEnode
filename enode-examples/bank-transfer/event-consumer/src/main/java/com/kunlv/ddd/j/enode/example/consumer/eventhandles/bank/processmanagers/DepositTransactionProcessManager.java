package com.kunlv.ddd.j.enode.example.consumer.eventhandles.bank.processmanagers;

import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.common.io.Task;
import com.kunlv.ddd.j.enode.core.annotation.Event;
import com.kunlv.ddd.j.enode.core.annotation.Subscribe;
import com.kunlv.ddd.j.enode.core.commanding.ICommandService;
import com.kunlv.ddd.j.enode.example.commands.bank.AddTransactionPreparationCommand;
import com.kunlv.ddd.j.enode.example.commands.bank.CommitTransactionPreparationCommand;
import com.kunlv.ddd.j.enode.example.commands.bank.ConfirmDepositCommand;
import com.kunlv.ddd.j.enode.example.commands.bank.ConfirmDepositPreparationCommand;
import com.kunlv.ddd.j.enode.example.domain.bank.TransactionType;
import com.kunlv.ddd.j.enode.example.domain.bank.bankaccount.PreparationType;
import com.kunlv.ddd.j.enode.example.domain.bank.bankaccount.TransactionPreparationAddedEvent;
import com.kunlv.ddd.j.enode.example.domain.bank.bankaccount.TransactionPreparationCommittedEvent;
import com.kunlv.ddd.j.enode.example.domain.bank.deposittransaction.DepositTransactionPreparationCompletedEvent;
import com.kunlv.ddd.j.enode.example.domain.bank.deposittransaction.DepositTransactionStartedEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 银行存款交易流程管理器，用于协调银行存款交易流程中各个参与者聚合根之间的消息交互
 * IMessageHandler<DepositTransactionStartedEvent>,                    //存款交易已开始
 * IMessageHandler<DepositTransactionPreparationCompletedEvent>,       //存款交易已提交
 * IMessageHandler<TransactionPreparationAddedEvent>,                  //账户预操作已添加
 * IMessageHandler<TransactionPreparationCommittedEvent>               //账户预操作已提交
 */
@Event
public class DepositTransactionProcessManager {
    @Autowired
    private ICommandService _commandService;

    @Subscribe
    public AsyncTaskResult handleAsync(DepositTransactionStartedEvent event) {
        AddTransactionPreparationCommand command = new AddTransactionPreparationCommand(
                event.AccountId,
                event.getAggregateRootId(),
                TransactionType.DepositTransaction,
                PreparationType.CreditPreparation,
                event.Amount);
        command.setId(event.getId());
        return Task.await(_commandService.sendAsync(command));
    }

    @Subscribe
    public AsyncTaskResult handleAsync(TransactionPreparationAddedEvent event) {
        if (event.TransactionPreparation.transactionType == TransactionType.DepositTransaction
                && event.TransactionPreparation.preparationType == PreparationType.CreditPreparation) {
            ConfirmDepositPreparationCommand command = new ConfirmDepositPreparationCommand(event.TransactionPreparation.TransactionId);
            command.setId(event.getId());
            return Task.await(_commandService.sendAsync(command));
        }
        return AsyncTaskResult.Success;
    }

    @Subscribe
    public AsyncTaskResult handleAsync(DepositTransactionPreparationCompletedEvent event) {
        CommitTransactionPreparationCommand command = new CommitTransactionPreparationCommand(event.AccountId, event.getAggregateRootId());
        command.setId(event.getId());
        return Task.await(_commandService.sendAsync(command));
    }

    @Subscribe
    public AsyncTaskResult handleAsync(TransactionPreparationCommittedEvent event) {
        if (event.TransactionPreparation.transactionType == TransactionType.DepositTransaction &&
                event.TransactionPreparation.preparationType == PreparationType.CreditPreparation) {
            ConfirmDepositCommand command = new ConfirmDepositCommand(event.TransactionPreparation.TransactionId);
            command.setId(event.getId());
            return Task.await(_commandService.sendAsync(command));
        }
        return AsyncTaskResult.Success;
    }
}
