package com.kunlv.ddd.j.enode.core.queue.command;

import com.kunlv.ddd.j.enode.common.serializing.JsonTool;
import com.kunlv.ddd.j.enode.core.commanding.ICommand;
import com.kunlv.ddd.j.enode.core.commanding.ICommandProcessor;
import com.kunlv.ddd.j.enode.core.commanding.ProcessingCommand;
import com.kunlv.ddd.j.enode.core.commanding.impl.CommandExecuteContext;
import com.kunlv.ddd.j.enode.core.domain.IAggregateStorage;
import com.kunlv.ddd.j.enode.core.domain.IRepository;
import com.kunlv.ddd.j.enode.core.infrastructure.ITypeNameProvider;
import com.kunlv.ddd.j.enode.core.queue.IMessageContext;
import com.kunlv.ddd.j.enode.core.queue.IMessageHandler;
import com.kunlv.ddd.j.enode.core.queue.QueueMessage;
import com.kunlv.ddd.j.enode.core.queue.SendReplyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCommandListener implements IMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractCommandListener.class);
    @Autowired
    protected SendReplyService sendReplyService;
    @Autowired
    protected ITypeNameProvider typeNameProvider;
    @Autowired
    protected ICommandProcessor commandProcessor;
    @Autowired
    protected IRepository repository;
    @Autowired
    protected IAggregateStorage aggregateRootStorage;

    public AbstractCommandListener setSendReplyService(SendReplyService sendReplyService) {
        this.sendReplyService = sendReplyService;
        return this;
    }

    public AbstractCommandListener setTypeNameProvider(ITypeNameProvider typeNameProvider) {
        this.typeNameProvider = typeNameProvider;
        return this;
    }

    public AbstractCommandListener setCommandProcessor(ICommandProcessor commandProcessor) {
        this.commandProcessor = commandProcessor;
        return this;
    }

    public AbstractCommandListener setRepository(IRepository repository) {
        this.repository = repository;
        return this;
    }

    public AbstractCommandListener setAggregateRootStorage(IAggregateStorage aggregateRootStorage) {
        this.aggregateRootStorage = aggregateRootStorage;
        return this;
    }

    @Override
    public void handle(QueueMessage queueMessage, IMessageContext context) {
        Map<String, String> commandItems = new HashMap<>();
        CommandMessage commandMessage = JsonTool.deserialize(queueMessage.getBody(), CommandMessage.class);
        Class commandType = typeNameProvider.getType(commandMessage.getCommandType());
        ICommand command = (ICommand) JsonTool.deserialize(commandMessage.getCommandData(), commandType);
        CommandExecuteContext commandExecuteContext = new CommandExecuteContext(repository, aggregateRootStorage, queueMessage, context, commandMessage, sendReplyService);
        commandItems.put("CommandReplyAddress", commandMessage.getReplyAddress());
        if (logger.isDebugEnabled()) {
            logger.debug("ENode command message received, messageId: {}, aggregateRootId: {}", command.getId(), command.getAggregateRootId());
        }
        commandProcessor.process(new ProcessingCommand(command, commandExecuteContext, commandItems));
    }
}
