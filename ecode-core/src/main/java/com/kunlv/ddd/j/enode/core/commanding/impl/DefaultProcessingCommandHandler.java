package com.kunlv.ddd.j.enode.core.commanding.impl;

import com.kunlv.ddd.j.enode.core.commanding.*;
import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.common.io.AsyncTaskStatus;
import com.kunlv.ddd.j.enode.common.io.IOHelper;
import com.kunlv.ddd.j.enode.common.io.IORuntimeException;
import com.kunlv.ddd.j.enode.common.logging.ENodeLogger;
import com.kunlv.ddd.j.enode.common.serializing.IJsonSerializer;
import com.kunlv.ddd.j.enode.core.domain.IAggregateRoot;
import com.kunlv.ddd.j.enode.core.domain.IMemoryCache;
import com.kunlv.ddd.j.enode.core.eventing.*;
import com.kunlv.ddd.j.enode.core.infrastructure.*;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IApplicationMessage;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessagePublisher;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.MessageHandlerData;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultProcessingCommandHandler implements IProcessingCommandHandler {
    private static final Logger _logger = ENodeLogger.getLog();

    private IJsonSerializer _jsonSerializer;
    private IEventStore _eventStore;
    private ICommandHandlerProvider _commandHandlerProvider;
    private ICommandAsyncHandlerProvider _commandAsyncHandlerProvider;
    private ITypeNameProvider _typeNameProvider;
    private IEventService _eventService;
    private IMessagePublisher<IApplicationMessage> _applicationMessagePublisher;
    private IMessagePublisher<IPublishableException> _exceptionPublisher;
    private IMemoryCache _memoryCache;
    private IOHelper _ioHelper;

    @Inject
    public DefaultProcessingCommandHandler(
            IJsonSerializer jsonSerializer,
            IEventStore eventStore,
            ICommandHandlerProvider commandHandlerProvider,
            ICommandAsyncHandlerProvider commandAsyncHandlerProvider,
            ITypeNameProvider typeNameProvider,
            IEventService eventService,
            IMessagePublisher<IApplicationMessage> applicationMessagePublisher,
            IMessagePublisher<IPublishableException> exceptionPublisher,
            IMemoryCache memoryCache,
            IOHelper ioHelper) {
        _jsonSerializer = jsonSerializer;
        _eventStore = eventStore;
        _commandHandlerProvider = commandHandlerProvider;
        _commandAsyncHandlerProvider = commandAsyncHandlerProvider;
        _typeNameProvider = typeNameProvider;
        _eventService = eventService;
        _applicationMessagePublisher = applicationMessagePublisher;
        _exceptionPublisher = exceptionPublisher;
        _memoryCache = memoryCache;
        _ioHelper = ioHelper;
        _eventService.setProcessingCommandHandler(this);
    }

    @Override
    public void handle(ProcessingCommand processingCommand) {
        ICommand command = processingCommand.getMessage();

        if (command.getAggregateRootId() == null || command.getAggregateRootId().trim().equals("")) {
            String errorMessage = String.format("The aggregateRootId of command cannot be null or empty. commandType:%s, commandId:%s", command.getClass().getName(), command.id());
            _logger.error(errorMessage);
            completeCommand(processingCommand, CommandStatus.Failed, String.class.getName(), errorMessage);
            return;
        }

        HandlerFindResult<ICommandHandlerProxy> findResult = getCommandHandler(processingCommand, commandType -> _commandHandlerProvider.getHandlers(commandType));
        if (findResult.getFindStatus() == HandlerFindStatus.Found) {
            handleCommand(processingCommand, findResult.getFindHandler());
        } else if (findResult.getFindStatus() == HandlerFindStatus.TooManyHandlerData) {
            _logger.error("Found more than one command handler data, commandType:{}, commandId:{}", command.getClass().getName(), command.id());
            completeCommand(processingCommand, CommandStatus.Failed, String.class.getName(), "More than one command handler data found.");
        } else if (findResult.getFindStatus() == HandlerFindStatus.TooManyHandler) {
            _logger.error("Found more than one command handler, commandType:{}, commandId:{}", command.getClass().getName(), command.id());
            completeCommand(processingCommand, CommandStatus.Failed, String.class.getName(), "More than one command handler found.");
        } else if (findResult.getFindStatus() == HandlerFindStatus.NotFound) {
            HandlerFindResult<ICommandAsyncHandlerProxy> asyncFindResult = getCommandHandler(processingCommand, commandType -> _commandAsyncHandlerProvider.getHandlers(commandType));
            ICommandAsyncHandlerProxy commandAsyncHandler = asyncFindResult.getFindHandler();
            if (asyncFindResult.getFindStatus() == HandlerFindStatus.Found) {
                handleCommand(processingCommand, commandAsyncHandler);
            } else if (asyncFindResult.getFindStatus() == HandlerFindStatus.TooManyHandlerData) {
                _logger.error("Found more than one command async handler data, commandType:{}, commandId:{}", command.getClass().getName(), command.id());
                completeCommand(processingCommand, CommandStatus.Failed, String.class.getName(), "More than one command async handler data found.");
            } else if (asyncFindResult.getFindStatus() == HandlerFindStatus.TooManyHandler) {
                _logger.error("Found more than one command async handler, commandType:{}, commandId:{}", command.getClass().getName(), command.id());
                completeCommand(processingCommand, CommandStatus.Failed, String.class.getName(), "More than one command async handler found.");
            } else if (asyncFindResult.getFindStatus() == HandlerFindStatus.NotFound) {
                String errorMessage = String.format("No command handler found of command. commandType:%s, commandId:%s", command.getClass().getName(), command.id());
                _logger.error(errorMessage);
                completeCommand(processingCommand, CommandStatus.Failed, String.class.getName(), errorMessage);
            }
        }
    }

    private void handleCommand(ProcessingCommand processingCommand, ICommandHandlerProxy commandHandler) {
        ICommand command = processingCommand.getMessage();

        processingCommand.getCommandExecuteContext().clear();

        //调用command handler执行当前command
        boolean handleSuccess;
        try {
            commandHandler.handle(processingCommand.getCommandExecuteContext(), command);
            _logger.debug("Handle command success. handlerType:{}, commandType:{}, commandId:{}, aggregateRootId:{}",
                    commandHandler.getInnerObject().getClass().getName(),
                    command.getClass().getName(),
                    command.id(),
                    command.getAggregateRootId());
            handleSuccess = true;
        } catch (Exception ex) {
            handleExceptionAsync(processingCommand, commandHandler, ex);
            return;
        }

        //如果command执行成功，则提交执行后的结果
        if (handleSuccess) {
            try {
                commitAggregateChanges(processingCommand);
            } catch (Exception ex) {
                logCommandExecuteException(processingCommand, commandHandler, ex);
                completeCommand(processingCommand, CommandStatus.Failed, ex.getClass().getName(), "Unknown exception caught when committing changes of command.");
            }
        }
    }

    private void commitAggregateChanges(ProcessingCommand processingCommand) {
        ICommand command = processingCommand.getMessage();
        ICommandExecuteContext context = processingCommand.getCommandExecuteContext();
        List<IAggregateRoot> trackedAggregateRoots = context.getTrackedAggregateRoots();
        int dirtyAggregateRootCount = 0;
        IAggregateRoot dirtyAggregateRoot = null;
        List<IDomainEvent> changedEvents = null;

        for (IAggregateRoot aggregateRoot : trackedAggregateRoots) {
            List<IDomainEvent> events = aggregateRoot.getChanges();

            if (events.size() > 0) {
                dirtyAggregateRootCount++;
                if (dirtyAggregateRootCount > 1) {
                    String errorMessage = String.format("Detected more than one aggregate created or modified by command. commandType:%s, commandId:%s",
                            command.getClass().getName(),
                            command.id());
                    _logger.error(errorMessage);
                    completeCommand(processingCommand, CommandStatus.Failed, String.class.getName(), errorMessage);
                    return;
                }
                dirtyAggregateRoot = aggregateRoot;
                changedEvents = events;
            }
        }
        ;

        //如果当前command没有对任何聚合根做修改，框架仍然需要尝试获取该command之前是否有产生事件，
        //如果有，则需要将事件再次发布到MQ；如果没有，则完成命令，返回command的结果为NothingChanged。
        //之所以要这样做是因为有可能当前command上次执行的结果可能是事件持久化完成，但是发布到MQ未完成，然后那时正好机器断电宕机了；
        //这种情况下，如果机器重启，当前command对应的聚合根从eventstore恢复的聚合根是被当前command处理过后的；
        //所以如果该command再次被处理，可能对应的聚合根就不会再产生事件了；
        //所以，我们要考虑到这种情况，尝试再次发布该命令产生的事件到MQ；
        //否则，如果我们直接将当前command设置为完成，即对MQ进行ack操作，那该command的事件就永远不会再发布到MQ了，这样就无法保证CQRS数据的最终一致性了。
        if (dirtyAggregateRootCount == 0 || changedEvents == null || changedEvents.size() == 0) {
            processIfNoEventsOfCommand(processingCommand);
            return;
        }

        //构造出一个事件流对象
        DomainEventStream eventStream = buildDomainEventStream(dirtyAggregateRoot, changedEvents, processingCommand);

        //将事件流提交到EventStore
        _eventService.commitDomainEventAsync(new EventCommittingContext(dirtyAggregateRoot, eventStream, processingCommand));
    }

    private void processIfNoEventsOfCommand(ProcessingCommand processingCommand) {
        ICommand command = processingCommand.getMessage();

        _ioHelper.tryAsyncActionRecursively("ProcessIfNoEventsOfCommand",
                () -> _eventStore.findAsync(command.getAggregateRootId(), command.id()),
                result ->
                {
                    DomainEventStream existingEventStream = result.getData();
                    if (existingEventStream != null) {
                        _eventService.publishDomainEventAsync(processingCommand, existingEventStream);
                    } else {
                        completeCommand(processingCommand, CommandStatus.NothingChanged, String.class.getName(), processingCommand.getCommandExecuteContext().getResult());
                    }
                },
                () -> String.format("[commandId:%s]", command.id()),
                errorMessage -> _logger.error("Find event by commandId has unknown exception, the code should not be run to here, errorMessage: {}", errorMessage),
                true);
    }

    private DomainEventStream buildDomainEventStream(IAggregateRoot aggregateRoot, List<IDomainEvent> changedEvents, ProcessingCommand processingCommand) {
        String commandResult = processingCommand.getCommandExecuteContext().getResult();
        if (commandResult != null) {
            processingCommand.getItems().put("CommandResult", commandResult);
        }
        return new DomainEventStream(
                processingCommand.getMessage().id(),
                aggregateRoot.uniqueId(),
                _typeNameProvider.getTypeName(aggregateRoot.getClass()),
                aggregateRoot.version() + 1,
                new Date(),
                new ArrayList<>(changedEvents),
                processingCommand.getItems());
    }

    private void handleExceptionAsync(ProcessingCommand processingCommand, ICommandHandlerProxy commandHandler, Exception exception) {
        ICommand command = processingCommand.getMessage();

        _ioHelper.tryAsyncActionRecursively("FindEventByCommandIdAsync",
                () -> _eventStore.findAsync(command.getAggregateRootId(), command.id()),
                result -> {
                    DomainEventStream existingEventStream = result.getData();

                    if (existingEventStream != null) {
                        //这里，我们需要再重新做一遍发布事件这个操作；
                        //之所以要这样做是因为虽然该command产生的事件已经持久化成功，但并不表示事件已经发布出去了；
                        //因为有可能事件持久化成功了，但那时正好机器断电了，则发布事件就没有做；
                        _logger.info("handle command exception,and the command has consumed before,we will publish domain event again and try execute next command mailbox message.", exception);
                        _eventService.publishDomainEventAsync(processingCommand, existingEventStream);
                    } else {
                        //到这里，说明当前command执行遇到异常，然后当前command之前也没执行过，是第一次被执行。
                        //那就判断当前异常是否是需要被发布出去的异常，如果是，则发布该异常给所有消费者；否则，就记录错误日志；
                        //然后，认为该command处理失败即可；

                        Exception exp = exception;
                        if (exp instanceof WrappedRuntimeException)
                            exp = ((WrappedRuntimeException) exp).getException();

                        if (exp instanceof IPublishableException) {
                            IPublishableException publishableException = (IPublishableException) exp;
                            publishExceptionAsync(processingCommand, publishableException);
                        } else {
                            logCommandExecuteException(processingCommand, commandHandler, exp);
                            completeCommand(processingCommand, CommandStatus.Failed, exp.getClass().getName(), exp.getMessage());
                        }
                    }
                },
                () -> String.format("[commandId:%s]", command.id()),
                errorMessage -> _logger.error(String.format("Find event by commandId has unknown exception, the code should not be run to here, errorMessage: %s", errorMessage)),
                true
        );

    }

    private void publishExceptionAsync(ProcessingCommand processingCommand, IPublishableException exception) {
        _ioHelper.tryAsyncActionRecursively("PublishExceptionAsync",
                () -> _exceptionPublisher.publishAsync(exception),
                result ->

                        completeCommand(processingCommand, CommandStatus.Failed, exception.getClass().getName(), ((Exception) exception).getMessage())
                ,
                () ->
                {
                    Map<String, String> serializableInfo = new HashMap<>();
                    exception.serializeTo(serializableInfo);
                    String exceptionInfo = String.join(",", serializableInfo.entrySet().stream().map(x -> String.format("%s:%s", x.getKey(), x.getValue())).collect(Collectors.toList()));
                    return String.format("[commandId:%s, exceptionInfo:%s]", processingCommand.getMessage().id(), exceptionInfo);
                },
                errorMessage -> _logger.error(String.format("Publish event has unknown exception, the code should not be run to here, errorMessage: %s", errorMessage)),
                true);
    }

    /*private void notifyCommandExecuted(ProcessingCommand processingCommand, CommandStatus commandStatus, String resultType, String result) {
        processingCommand.complete(new CommandResult(commandStatus, processingCommand.getMessage().id(), processingCommand.getMessage().getAggregateRootId(), result, resultType));
    }

    private void retryCommand(ProcessingCommand processingCommand) {
        processingCommand.getCommandExecuteContext().clear();
        handleAsync(processingCommand);
    }*/

    private void logCommandExecuteException(ProcessingCommand processingCommand, ICommandHandlerProxy commandHandler, Exception exception) {
        ICommand command = processingCommand.getMessage();
        String errorMessage = String.format("%s raised when %s handling %s. commandId:%s, aggregateRootId:%s",
                exception.getClass().getName(),
                commandHandler.getInnerObject().getClass().getName(),
                command.getClass().getName(),
                command.id(),
                command.getAggregateRootId());
        _logger.error(errorMessage, exception);
    }

    /*private ICommandAsyncHandlerProxy getCommandAsyncHandler(ProcessingCommand processingCommand) {
        ICommand command = processingCommand.getMessage();
        List<MessageHandlerData<ICommandAsyncHandlerProxy>> commandAsyncHandlers = _commandAsyncHandlerProvider.getHandlers(command.getClass());

        if (commandAsyncHandlers.size() > 1) {
            _logger.error("Found more than one command handlers, commandType:%s, commandId:%s.", command.getClass().getName(), command.id());
            notifyCommandExecuted(processingCommand, CommandStatus.Failed, String.class.getName(), "More than one command handlers found.");
            return null;
        }

        MessageHandlerData<ICommandAsyncHandlerProxy> handlerData = commandAsyncHandlers.get(0);
        if (handlerData != null) {
            return handlerData.ListHandlers.get(0);
        }
        return null;
    }*/

    private void handleCommand(ProcessingCommand processingCommand, ICommandAsyncHandlerProxy commandHandler) {
        handleCommandAsync(processingCommand, commandHandler);
    }

    private void handleCommandAsync(ProcessingCommand processingCommand, ICommandAsyncHandlerProxy commandHandler) {
        ICommand command = processingCommand.getMessage();

        _ioHelper.tryAsyncActionRecursively("HandleCommandAsync",
                () ->
                {
                    try {
                        CompletableFuture<AsyncTaskResult<IApplicationMessage>> asyncResult = commandHandler.handleAsync(command);
                        _logger.debug("Handle command async success. handlerType:{}, commandType:{}, commandId:{}, aggregateRootId:{}",
                                commandHandler.getInnerObject().getClass().getName(),
                                command.getClass().getName(),
                                command.id(),
                                command.getAggregateRootId());
                        return asyncResult;
                    } catch (IORuntimeException ex) {
                        _logger.error(String.format("Handle command async has io exception. handlerType:%s, commandType:%s, commandId:%s, aggregateRootId:%s",
                                commandHandler.getInnerObject().getClass().getName(),
                                command.getClass().getName(),
                                command.id(),
                                command.getAggregateRootId()), ex);
                        return CompletableFuture.completedFuture(new AsyncTaskResult<>(AsyncTaskStatus.IOException, ex.getMessage()));
                    } catch (Exception ex) {
                        _logger.error(String.format("Handle command async has unknown exception. handlerType:%s, commandType:%s, commandId:%s, aggregateRootId:%s",
                                commandHandler.getInnerObject().getClass().getName(),
                                command.getClass().getName(),
                                command.id(),
                                command.getAggregateRootId()), ex);
                        return CompletableFuture.completedFuture(new AsyncTaskResult<>(AsyncTaskStatus.Failed, ex.getMessage()));
                    }
                },
                result ->
                        commitChangesAsync(processingCommand, true, result.getData(), null),
                () -> String.format("[command:[id:%s,type:%s],handlerType:%s]", command.id(), command.getClass().getName(), commandHandler.getInnerObject().getClass().getName()),
                errorMessage -> commitChangesAsync(processingCommand, false, null, errorMessage),
                false);
    }

    private void commitChangesAsync(ProcessingCommand processingCommand, boolean success, IApplicationMessage message, String errorMessage) {
        if (success) {
            if (message != null) {
                publishMessageAsync(processingCommand, message);
            } else {
                completeCommand(processingCommand, CommandStatus.Success, null, null);
            }
        } else {
            completeCommand(processingCommand, CommandStatus.Failed, String.class.getName(), errorMessage);
        }
    }

    private void publishMessageAsync(ProcessingCommand processingCommand, IApplicationMessage message) {
        ICommand command = processingCommand.getMessage();

        _ioHelper.tryAsyncActionRecursively("PublishApplicationMessageAsync",
                () -> _applicationMessagePublisher.publishAsync(message),
                result -> completeCommand(processingCommand, CommandStatus.Success, message.getTypeName(), _jsonSerializer.serialize(message)),
                () -> String.format("[application message:[id:%s,type:%s],command:[id:%s,type:%s]]", message.id(), message.getClass().getName(), command.id(), command.getClass().getName()),
                errorMessage -> _logger.error(String.format("Publish application message has unknown exception, the code should not be run to here, errorMessage: %s", errorMessage)),
                true);
    }

    private <T extends IObjectProxy> HandlerFindResult<T> getCommandHandler(ProcessingCommand processingCommand, Function<Class, List<MessageHandlerData<T>>> getHandlersFunc) {
        ICommand command = processingCommand.getMessage();
        List<MessageHandlerData<T>> handlerDataList = getHandlersFunc.apply(command.getClass());

        if (handlerDataList == null || handlerDataList.size() == 0) {
            return HandlerFindResult.NotFound;
        } else if (handlerDataList.size() > 1) {
            return HandlerFindResult.TooManyHandlerData;
        }

        MessageHandlerData<T> handlerData = handlerDataList.get(0);

        if (handlerData.ListHandlers == null || handlerData.ListHandlers.size() == 0) {
            return HandlerFindResult.NotFound;
        } else if (handlerData.ListHandlers.size() > 1) {
            return HandlerFindResult.TooManyHandler;
        }

        T handlerProxy = handlerData.ListHandlers.get(0);

        return new HandlerFindResult<>(HandlerFindStatus.Found, handlerProxy);
    }

    static class HandlerFindResult<T extends IObjectProxy> {

        static HandlerFindResult NotFound = new HandlerFindResult<>(HandlerFindStatus.NotFound);
        static HandlerFindResult TooManyHandlerData = new HandlerFindResult<>(HandlerFindStatus.TooManyHandlerData);
        static HandlerFindResult TooManyHandler = new HandlerFindResult<>(HandlerFindStatus.TooManyHandler);

        private HandlerFindStatus findStatus;
        private T findHandler;

        HandlerFindResult(HandlerFindStatus findStatus) {
            this(findStatus, null);
        }

        public HandlerFindResult(HandlerFindStatus findStatus, T findHandler) {
            this.findStatus = findStatus;
            this.findHandler = findHandler;
        }

        public HandlerFindStatus getFindStatus() {
            return findStatus;
        }

        public void setFindStatus(HandlerFindStatus findStatus) {
            this.findStatus = findStatus;
        }

        public T getFindHandler() {
            return findHandler;
        }

        public void setFindHandler(T findHandler) {
            this.findHandler = findHandler;
        }
    }

    private void completeCommand(ProcessingCommand processingCommand, CommandStatus commandStatus, String resultType, String result) {
        CommandResult commandResult = new CommandResult(commandStatus, processingCommand.getMessage().id(), processingCommand.getMessage().getAggregateRootId(), result, resultType);
        processingCommand.getMailbox().completeMessage(processingCommand, commandResult);
    }

    enum HandlerFindStatus {
        NotFound,
        Found,
        TooManyHandlerData,
        TooManyHandler
    }
}
