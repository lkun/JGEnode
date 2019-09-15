package com.kunlv.ddd.j.enode.example.consumer.commandhandles.bank;

import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.common.io.AsyncTaskStatus;
import com.kunlv.ddd.j.enode.common.io.Task;
import com.kunlv.ddd.j.enode.core.annotation.Command;
import com.kunlv.ddd.j.enode.core.annotation.Subscribe;
import com.kunlv.ddd.j.enode.core.applicationmessage.IApplicationMessage;
import com.kunlv.ddd.j.enode.core.commanding.ICommandContext;
import com.kunlv.ddd.j.enode.example.applicationmessages.AccountValidateFailedMessage;
import com.kunlv.ddd.j.enode.example.applicationmessages.AccountValidatePassedMessage;
import com.kunlv.ddd.j.enode.example.commands.bank.AddTransactionPreparationCommand;
import com.kunlv.ddd.j.enode.example.commands.bank.CommitTransactionPreparationCommand;
import com.kunlv.ddd.j.enode.example.commands.bank.CreateAccountCommand;
import com.kunlv.ddd.j.enode.example.commands.bank.ValidateAccountCommand;
import com.kunlv.ddd.j.enode.example.domain.bank.bankaccount.BankAccount;

import java.util.concurrent.CompletableFuture;

/**
 * 银行账户相关命令处理
 * ICommandHandler<CreateAccountCommand>,                       //开户
 * ICommandAsyncHandler<ValidateAccountCommand>,                //验证账户是否合法
 * ICommandHandler<AddTransactionPreparationCommand>,           //添加预操作
 * ICommandHandler<CommitTransactionPreparationCommand>         //提交预操作
 */
@Command
public class BankAccountCommandHandler {
    /**
     * 开户
     */
    @Subscribe
    public void handleAsync(ICommandContext context, CreateAccountCommand command) {
        context.addAsync(new BankAccount(command.getAggregateRootId(), command.Owner));
    }

    /**
     * 添加预操作
     */
    @Subscribe
    public void handleAsync(ICommandContext context, AddTransactionPreparationCommand command) {
        CompletableFuture<BankAccount> future = context.getAsync(command.getAggregateRootId(), BankAccount.class);
        BankAccount account = Task.await(future);
        account.AddTransactionPreparation(command.TransactionId, command.TransactionType, command.PreparationType, command.Amount);
    }

    /**
     * 验证账户是否合法
     *
     * @param command
     * @return
     */
    @Subscribe
    public AsyncTaskResult<IApplicationMessage> handleAsync(ValidateAccountCommand command) {
        IApplicationMessage applicationMessage = new AccountValidatePassedMessage(command.getAggregateRootId(), command.TransactionId);
        //此处应该会调用外部接口验证账号是否合法，这里仅仅简单通过账号是否以INVALID字符串开头来判断是否合法；根据账号的合法性，返回不同的应用层消息
        if (command.getAggregateRootId().startsWith("INVALID")) {
            applicationMessage = new AccountValidateFailedMessage(command.getAggregateRootId(), command.TransactionId, "账户不合法.");
        }
        return new AsyncTaskResult<>(AsyncTaskStatus.Success, applicationMessage);
    }

    /**
     * 提交预操作
     *
     * @param context
     * @param command
     * @return
     */
    @Subscribe
    public void handleAsync(ICommandContext context, CommitTransactionPreparationCommand command) {
        CompletableFuture<BankAccount> future = context.getAsync(command.getAggregateRootId(), BankAccount.class);
        BankAccount account = Task.await(future);
        account.CommitTransactionPreparation(command.TransactionId);
    }
}
