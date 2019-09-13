package com.kunlv.ddd.j.enode.rocketmq.message;

import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.common.io.AsyncTaskStatus;
import com.kunlv.ddd.j.enode.common.utilities.Ensure;
import com.kunlv.ddd.j.enode.core.commanding.CommandResult;
import com.kunlv.ddd.j.enode.core.commanding.CommandReturnType;
import com.kunlv.ddd.j.enode.core.commanding.ICommand;
import com.kunlv.ddd.j.enode.core.queue.QueueMessage;
import com.kunlv.ddd.j.enode.core.queue.command.AbstractCommandService;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.CompletableFuture;

/**
 * @author lvk618@gmail.com
 */
public class RocketMQCommandService extends AbstractCommandService {
    private DefaultMQProducer defaultMQProducer;

    @Override
    public CompletableFuture<AsyncTaskResult> sendAsync(ICommand command) {
        try {
            QueueMessage queueMessage = buildCommandMessage(command, false);
            Message message = RocketMQTool.covertToProducerRecord(queueMessage);
            return SendRocketMQService.sendMessageAsync(defaultMQProducer, message, queueMessage.getRouteKey());
        } catch (Exception ex) {
            return CompletableFuture.completedFuture(new AsyncTaskResult<>(AsyncTaskStatus.Failed, ex.getMessage()));
        }
    }

    @Override
    public CompletableFuture<AsyncTaskResult<CommandResult>> executeAsync(ICommand command) {
        return executeAsync(command, CommandReturnType.CommandExecuted);
    }

    @Override
    public CompletableFuture<AsyncTaskResult<CommandResult>> executeAsync(ICommand command, CommandReturnType commandReturnType) {
        CompletableFuture<AsyncTaskResult<CommandResult>> taskCompletionSource = new CompletableFuture<>();
        try {
            Ensure.notNull(commandResultProcessor, "commandResultProcessor");
            commandResultProcessor.registerProcessingCommand(command, commandReturnType, taskCompletionSource);
            QueueMessage queueMessage = buildCommandMessage(command, true);
            Message message = RocketMQTool.covertToProducerRecord(queueMessage);
            CompletableFuture<AsyncTaskResult> sendMessageAsync = SendRocketMQService.sendMessageAsync(defaultMQProducer, message, queueMessage.getRouteKey());
            sendMessageAsync.thenAccept(sendResult -> {
                if (sendResult.getStatus().equals(AsyncTaskStatus.Success)) {
                    //_commandResultProcessor中会继续等命令或事件处理完成的状态
                    // 直接返回taskCompletionSource
                } else {
                    commandResultProcessor.processFailedSendingCommand(command);
                    taskCompletionSource.complete(new AsyncTaskResult<>(sendResult.getStatus(), sendResult.getErrorMessage()));
                }
            });
        } catch (Exception ex) {
            taskCompletionSource.complete(new AsyncTaskResult<>(AsyncTaskStatus.Failed, ex.getMessage()));
        }
        return taskCompletionSource;
    }

    public DefaultMQProducer getDefaultMQProducer() {
        return defaultMQProducer;
    }

    public void setDefaultMQProducer(DefaultMQProducer defaultMQProducer) {
        this.defaultMQProducer = defaultMQProducer;
    }
}
