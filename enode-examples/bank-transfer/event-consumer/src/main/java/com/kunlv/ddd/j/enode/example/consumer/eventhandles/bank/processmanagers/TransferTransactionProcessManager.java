package com.kunlv.ddd.j.enode.example.consumer.eventhandles.bank.processmanagers;


import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.common.io.Task;
import com.kunlv.ddd.j.enode.core.annotation.Event;
import com.kunlv.ddd.j.enode.core.annotation.Subscribe;
import com.kunlv.ddd.j.enode.core.commanding.ICommandService;
import com.kunlv.ddd.j.enode.example.applicationmessages.AccountValidateFailedMessage;
import com.kunlv.ddd.j.enode.example.applicationmessages.AccountValidatePassedMessage;
import com.kunlv.ddd.j.enode.example.commands.bank.*;
import com.kunlv.ddd.j.enode.example.domain.bank.TransactionType;
import com.kunlv.ddd.j.enode.example.domain.bank.bankaccount.InsufficientBalanceException;
import com.kunlv.ddd.j.enode.example.domain.bank.bankaccount.PreparationType;
import com.kunlv.ddd.j.enode.example.domain.bank.bankaccount.TransactionPreparationAddedEvent;
import com.kunlv.ddd.j.enode.example.domain.bank.bankaccount.TransactionPreparationCommittedEvent;
import com.kunlv.ddd.j.enode.example.domain.bank.transfertransaction.AccountValidatePassedConfirmCompletedEvent;
import com.kunlv.ddd.j.enode.example.domain.bank.transfertransaction.TransferInPreparationConfirmedEvent;
import com.kunlv.ddd.j.enode.example.domain.bank.transfertransaction.TransferOutPreparationConfirmedEvent;
import com.kunlv.ddd.j.enode.example.domain.bank.transfertransaction.TransferTransactionStartedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;

/**
 * IMessageHandler<TransferTransactionStartedEvent>,                  //转账交易已开始
 * IMessageHandler<AccountValidatePassedMessage>,                     //账户验证已通过
 * IMessageHandler<AccountValidateFailedMessage>,                     //账户验证未通过
 * IMessageHandler<AccountValidatePassedConfirmCompletedEvent>,       //两个账户的验证通过事件都已确认
 * IMessageHandler<TransactionPreparationAddedEvent>,                 //账户预操作已添加
 * IMessageHandler<InsufficientBalanceException>,                     //账户余额不足
 * IMessageHandler<TransferOutPreparationConfirmedEvent>,             //转账交易预转出已确认
 * IMessageHandler<TransferInPreparationConfirmedEvent>,              //转账交易预转入已确认
 * IMessageHandler<TransactionPreparationCommittedEvent>              //账户预操作已提交
 */
@Event
public class TransferTransactionProcessManager {
    @Autowired
    private ICommandService commandService;

    @Subscribe
    public AsyncTaskResult handleAsync(TransferTransactionStartedEvent evnt) {
        ValidateAccountCommand command = new ValidateAccountCommand(evnt.TransactionInfo.SourceAccountId, evnt.getAggregateRootId());
        command.setId(evnt.getId());
        ValidateAccountCommand targetCommand = new ValidateAccountCommand(evnt.TransactionInfo.TargetAccountId, evnt.getAggregateRootId());
        targetCommand.setId(evnt.getId());
        CompletableFuture task1 = commandService.sendAsync(command);
        CompletableFuture task2 = commandService.sendAsync(targetCommand);
        CompletableFuture.allOf(task1, task2);
        return AsyncTaskResult.Success;
    }

    @Subscribe
    public AsyncTaskResult handleAsync(AccountValidatePassedMessage message) {
        ConfirmAccountValidatePassedCommand command = new ConfirmAccountValidatePassedCommand(message.TransactionId, message.AccountId);
        command.setId(message.getId());
        return Task.await(commandService.sendAsync(command));
    }

    @Subscribe
    public AsyncTaskResult handleAsync(AccountValidateFailedMessage message) {
        CancelTransferTransactionCommand command = new CancelTransferTransactionCommand(message.TransactionId);
        command.setId(message.getId());
        return Task.await(commandService.sendAsync(command));
    }

    @Subscribe
    public AsyncTaskResult handleAsync(AccountValidatePassedConfirmCompletedEvent evnt) {
        AddTransactionPreparationCommand command = new AddTransactionPreparationCommand(
                evnt.TransactionInfo.SourceAccountId,
                evnt.getAggregateRootId(),
                TransactionType.TransferTransaction,
                PreparationType.DebitPreparation,
                evnt.TransactionInfo.Amount);
        command.setId(evnt.getId());
        return Task.await(commandService.sendAsync(command));
    }

    @Subscribe
    public AsyncTaskResult handleAsync(TransactionPreparationAddedEvent evnt) {
        if (evnt.TransactionPreparation.transactionType == TransactionType.TransferTransaction) {
            if (evnt.TransactionPreparation.preparationType == PreparationType.DebitPreparation) {
                ConfirmTransferOutPreparationCommand command = new ConfirmTransferOutPreparationCommand(evnt.TransactionPreparation.TransactionId);
                command.setId(evnt.getId());
                return Task.await(commandService.sendAsync(command));
            } else if (evnt.TransactionPreparation.preparationType == PreparationType.CreditPreparation) {
                ConfirmTransferInPreparationCommand command = new ConfirmTransferInPreparationCommand(evnt.TransactionPreparation.TransactionId);
                command.setId(evnt.getId());
                return Task.await(commandService.sendAsync(command));
            }
        }
        return AsyncTaskResult.Success;
    }

    @Subscribe
    public AsyncTaskResult handleAsync(InsufficientBalanceException exception) {
        if (exception.TransactionType == TransactionType.TransferTransaction) {
            CancelTransferTransactionCommand command = new CancelTransferTransactionCommand(exception.TransactionId);
            command.setId(exception.getId());
            return Task.await(commandService.sendAsync(command));
        }
        return AsyncTaskResult.Success;
    }

    @Subscribe
    public AsyncTaskResult handleAsync(TransferOutPreparationConfirmedEvent evnt) {
        AddTransactionPreparationCommand command = new AddTransactionPreparationCommand(
                evnt.TransactionInfo.TargetAccountId,
                evnt.getAggregateRootId(),
                TransactionType.TransferTransaction,
                PreparationType.CreditPreparation,
                evnt.TransactionInfo.Amount);
        command.setId(evnt.getId());
        return Task.await(commandService.sendAsync(command));
    }

    @Subscribe
    public AsyncTaskResult handleAsync(TransferInPreparationConfirmedEvent evnt) {
        CommitTransactionPreparationCommand command = new CommitTransactionPreparationCommand(evnt.TransactionInfo.SourceAccountId, evnt.getAggregateRootId());
        command.setId(evnt.getId());
        CommitTransactionPreparationCommand targetCommand = new CommitTransactionPreparationCommand(evnt.TransactionInfo.TargetAccountId, evnt.getAggregateRootId());
        targetCommand.setId(evnt.getId());
        CompletableFuture task1 = commandService.sendAsync(command);
        CompletableFuture task2 = commandService.sendAsync(targetCommand);
        return AsyncTaskResult.Success;
    }

    @Subscribe
    public AsyncTaskResult handleAsync(TransactionPreparationCommittedEvent evnt) {
        if (evnt.TransactionPreparation.transactionType == TransactionType.TransferTransaction) {
            if (evnt.TransactionPreparation.preparationType == PreparationType.DebitPreparation) {
                ConfirmTransferOutCommand command = new ConfirmTransferOutCommand(evnt.TransactionPreparation.TransactionId);
                command.setId(evnt.getId());
                return Task.await(commandService.sendAsync(command));
            } else if (evnt.TransactionPreparation.preparationType == PreparationType.CreditPreparation) {
                ConfirmTransferInCommand command = new ConfirmTransferInCommand(evnt.TransactionPreparation.TransactionId);
                command.setId(evnt.getId());
                return Task.await(commandService.sendAsync(command));
            }
        }
        return AsyncTaskResult.Success;
    }
}
