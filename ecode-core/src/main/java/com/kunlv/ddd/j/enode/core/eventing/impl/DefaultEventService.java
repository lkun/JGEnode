package com.kunlv.ddd.j.enode.core.eventing.impl;

import com.kunlv.ddd.j.enode.core.ENode;
import com.kunlv.ddd.j.enode.core.commanding.*;
import com.kunlv.ddd.j.enode.common.io.IOHelper;
import com.kunlv.ddd.j.enode.common.logging.ENodeLogger;
import com.kunlv.ddd.j.enode.common.scheduling.IScheduleService;
import com.kunlv.ddd.j.enode.core.domain.IMemoryCache;
import com.kunlv.ddd.j.enode.core.eventing.*;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessagePublisher;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class DefaultEventService implements IEventService {
    private static final Logger _logger = ENodeLogger.getLog();

    private IProcessingCommandHandler _processingCommandHandler;
    private final ConcurrentMap<String, EventMailBox> _mailboxDict;
    private final IScheduleService _scheduleService;
    private final IMemoryCache _memoryCache;
    private final IEventStore _eventStore;
    private final IMessagePublisher<DomainEventStreamMessage> _domainEventPublisher;
    private final IOHelper _ioHelper;
    private final int _batchSize;
    private final int _timeoutSeconds;
    private final String _taskName;

    @Inject
    public DefaultEventService(
            IScheduleService scheduleService,
            IMemoryCache memoryCache,
            IEventStore eventStore,
            IMessagePublisher<DomainEventStreamMessage> domainEventPublisher,
            IOHelper ioHelper) {
        _mailboxDict = new ConcurrentHashMap<>();
        _scheduleService = scheduleService;
        _ioHelper = ioHelper;
        _memoryCache = memoryCache;
        _eventStore = eventStore;
        _domainEventPublisher = domainEventPublisher;
        _batchSize = ENode.getInstance().getSetting().getEventMailBoxPersistenceMaxBatchSize();
        _timeoutSeconds = ENode.getInstance().getSetting().getAggregateRootMaxInactiveSeconds();
        _taskName = "CleanInactiveAggregates_" + System.nanoTime() + new Random().nextInt(10000);
    }

    @Override
    public void setProcessingCommandHandler(IProcessingCommandHandler processingCommandHandler) {
        _processingCommandHandler = processingCommandHandler;
    }

    @Override
    public void commitDomainEventAsync(EventCommittingContext context) {
        EventMailBox eventMailbox = _mailboxDict.computeIfAbsent(context.getAggregateRoot().uniqueId(), x ->
                new EventMailBox(x, _batchSize, committingContexts ->
                {
                    if (committingContexts == null || committingContexts.size() == 0) {
                        return;
                    }
                    if (_eventStore.isSupportBatchAppendEvent()) {
                        batchPersistEventAsync(committingContexts);
                    } else {
                        persistEventOneByOne(committingContexts);
                    }
                })
        );

        eventMailbox.enqueueMessage(context);
        refreshAggregateMemoryCache(context);
    }

    @Override
    public void publishDomainEventAsync(ProcessingCommand processingCommand, DomainEventStream eventStream) {
        if (eventStream.items() == null || eventStream.items().size() == 0) {
            eventStream.setItems(processingCommand.getItems());
        }
        DomainEventStreamMessage eventStreamMessage = new DomainEventStreamMessage(processingCommand.getMessage().id(), eventStream.aggregateRootId(),
                eventStream.version(), eventStream.aggregateRootTypeName(), eventStream.events(), eventStream.items());
        publishDomainEventAsyncRecursively(processingCommand, eventStreamMessage);
    }

    @Override
    public void start() {
        _scheduleService.startTask(_taskName, this::cleanInactiveMailbox, ENode.getInstance().getSetting().getScanExpiredAggregateIntervalMilliseconds(), ENode.getInstance().getSetting().getScanExpiredAggregateIntervalMilliseconds());
    }

    @Override
    public void stop() {
        _scheduleService.stopTask(_taskName);
    }

    private void batchPersistEventAsync(List<EventCommittingContext> committingContexts) {
        _ioHelper.tryActionRecursively("BatchPersistEventAsync",
                () -> _eventStore.batchAppend(committingContexts.stream().map(x -> x.getEventStream()).collect(Collectors.toList())),
                result ->
                {
                    EventMailBox eventMailBox = committingContexts.get(0).getEventMailBox();
                    EventAppendResult appendResult = result.getData();
                    if (appendResult == EventAppendResult.Success) {
                        _logger.debug("Batch persist event success, aggregateRootId: {}, eventStreamCount: {}", eventMailBox.getAggregateRootId(), committingContexts.size());

                        CompletableFuture.runAsync(() ->
                                committingContexts.stream().forEach(context -> publishDomainEventAsync(context.getProcessingCommand(), context.getEventStream()))
                        );

                        eventMailBox.tryRun(true);
                    } else if (appendResult == EventAppendResult.DuplicateEvent) {
                        EventCommittingContext context = committingContexts.get(0);
                        if (context.getEventStream().version() == 1) {
                            handleFirstEventDuplicationAsync(context);
                        } else {
                            _logger.warn("Batch persist event has concurrent version conflict, first eventStream: {}, batchSize: {}", context.getEventStream(), committingContexts.size());
                            resetCommandMailBoxConsumingSequence(context, context.getProcessingCommand().getSequence());
                        }
                    } else if (appendResult == EventAppendResult.DuplicateCommand) {
                        persistEventOneByOne(committingContexts);
                    }
                },
                () -> String.format("[contextListCount:%d]", committingContexts.size()),
                errorMessage ->
                        _logger.error(String.format("Batch persist event has unknown exception, the code should not be run to here, errorMessage: {}", errorMessage)),
                true);
    }

    private void persistEventOneByOne(List<EventCommittingContext> contextList) {
        ConcatContexts(contextList);
        persistEvent(contextList.get(0));
    }

    private void persistEvent(EventCommittingContext context) {
        IOHelper.tryActionRecursively("PersistEvent",
                () -> _eventStore.append(context.getEventStream()),
                result -> {
                    if (result.getData() == EventAppendResult.Success) {
                        _logger.debug("Persist events success, {}", context.getEventStream());
                        publishDomainEventAsync(context.getProcessingCommand(), context.getEventStream());

                        if (context.getNext() != null) {
                            persistEvent(context.getNext());
                        } else {
                            context.getEventMailBox().tryRun(true);
                        }
                    } else if (result.getData() == EventAppendResult.DuplicateEvent) {
                        //如果是当前事件的版本号为1，则认为是在创建重复的聚合根
                        if (context.getEventStream().version() == 1) {
                            handleFirstEventDuplicationAsync(context);
                        }
                        //如果事件的版本大于1，则认为是更新聚合根时遇到并发冲突了，则需要进行重试；
                        else {
                            _logger.warn("Persist event has concurrent version conflict, eventStream: {}", context.getEventStream());
                            resetCommandMailBoxConsumingSequence(context, context.getProcessingCommand().getSequence());
                        }
                    } else if (result.getData() == EventAppendResult.DuplicateCommand) {
                        _logger.warn("Persist event has duplicate command, eventStream: {}", context.getEventStream());
                        resetCommandMailBoxConsumingSequence(context, context.getProcessingCommand().getSequence() + 1);
                        tryToRepublishEventAsync(context);
                    }
                },
                () -> String.format("[eventStream:%s]", context.getEventStream()),
                errorMessage -> _logger.error(String.format("Persist event has unknown exception, the code should not be run to here, errorMessage: %s", errorMessage)),
                true);
    }

    private void resetCommandMailBoxConsumingSequence(EventCommittingContext context, long consumingSequence) {
        EventMailBox eventMailBox = context.getEventMailBox();
        ProcessingCommand processingCommand = context.getProcessingCommand();
        ICommand command = processingCommand.getMessage();
        ProcessingCommandMailbox commandMailBox = processingCommand.getMailbox();

        commandMailBox.pause();
        try {
            refreshAggregateMemoryCacheToLatestVersion(context.getEventStream().aggregateRootTypeName(), context.getEventStream().aggregateRootId());
            commandMailBox.resetConsumingSequence(consumingSequence);
            eventMailBox.clear();
            eventMailBox.exit();
            _logger.info("ResetCommandMailBoxConsumingSequence success, commandId: {}, aggregateRootId: {}, consumingSequence: {}", command.id(), command.getAggregateRootId(), consumingSequence);

        } catch (Exception ex) {
            _logger.error(String.format("ResetCommandMailBoxConsumingOffset has unknown exception, commandId: %s, aggregateRootId: %s", command.id(), command.getAggregateRootId()), ex);
        } finally {
            commandMailBox.resume();
        }
    }

    private void tryToRepublishEventAsync(EventCommittingContext context) {
        ICommand command = context.getProcessingCommand().getMessage();

        _ioHelper.tryAsyncActionRecursively("FindEventByCommandIdAsync",
                () -> _eventStore.findAsync(context.getEventStream().aggregateRootId(), command.id()),
                result ->
                {
                    DomainEventStream existingEventStream = result.getData();
                    if (existingEventStream != null) {
                        //这里，我们需要再重新做一遍发布事件这个操作；
                        //之所以要这样做是因为虽然该command产生的事件已经持久化成功，但并不表示事件已经发布出去了；
                        //因为有可能事件持久化成功了，但那时正好机器断电了，则发布事件都没有做；
                        publishDomainEventAsync(context.getProcessingCommand(), existingEventStream);
                    } else {
                        //到这里，说明当前command想添加到eventStore中时，提示command重复，但是尝试从eventStore中取出该command时却找不到该command。
                        //出现这种情况，我们就无法再做后续处理了，这种错误理论上不会出现，除非eventStore的Add接口和Get接口出现读写不一致的情况；
                        //框架会记录错误日志，让开发者排查具体是什么问题。
                        String errorMessage = String.format("Command should be exist in the event store, but we cannot find it from the event store, this should not be happen, and we cannot continue again. commandType:%s, commandId:%s, aggregateRootId:%s",
                                command.getClass().getName(),
                                command.id(),
                                context.getEventStream().aggregateRootId());
                        _logger.error(errorMessage);

                        CommandResult commandResult = new CommandResult(CommandStatus.Failed, command.id(), command.getAggregateRootId(), "Command should be exist in the event store, but we cannot find it from the event store.", String.class.getName());
                        completeCommand(context.getProcessingCommand(), commandResult);
                    }
                },
                () -> String.format("[aggregateRootId:%s, commandId:%s]", command.getAggregateRootId(), command.id()),
                errorMessage ->
                {
                    _logger.error(String.format("Find event by commandId has unknown exception, the code should not be run to here, errorMessage: %s", errorMessage));
                },
                true);
    }

    private void handleFirstEventDuplicationAsync(EventCommittingContext context) {
        DomainEventStream eventStream = context.getEventStream();

        _ioHelper.tryAsyncActionRecursively("FindFirstEventByVersion",
                () -> _eventStore.findAsync(eventStream.aggregateRootId(), 1),
                result ->
                {
                    DomainEventStream firstEventStream = result.getData();
                    if (firstEventStream != null) {
                        //判断是否是同一个command，如果是，则再重新做一遍发布事件；
                        //之所以要这样做，是因为虽然该command产生的事件已经持久化成功，但并不表示事件也已经发布出去了；
                        //有可能事件持久化成功了，但那时正好机器断电了，则发布事件都没有做；
                        if (context.getProcessingCommand().getMessage().id().equals(firstEventStream.commandId())) {
                            resetCommandMailBoxConsumingSequence(context, context.getProcessingCommand().getSequence() + 1);
                            publishDomainEventAsync(context.getProcessingCommand(), firstEventStream);
                        } else {
                            //如果不是同一个command，则认为是两个不同的command重复创建ID相同的聚合根，我们需要记录错误日志，然后通知当前command的处理完成；
                            String errorMessage = String.format("Duplicate aggregate creation. current commandId:%s, existing commandId:%s, aggregateRootId:%s, aggregateRootTypeName:%s",
                                    context.getProcessingCommand().getMessage().id(),
                                    firstEventStream.commandId(),
                                    firstEventStream.aggregateRootId(),
                                    firstEventStream.aggregateRootTypeName());
                            _logger.error(errorMessage);
                            resetCommandMailBoxConsumingSequence(context, context.getProcessingCommand().getSequence() + 1);
                            CommandResult commandResult = new CommandResult(CommandStatus.Failed, context.getProcessingCommand().getMessage().id(), eventStream.aggregateRootId(), "Duplicate aggregate creation.", String.class.getName());
                            completeCommand(context.getProcessingCommand(), commandResult);
                        }
                    } else {
                        String errorMessage = String.format("Duplicate aggregate creation, but we cannot find the existing eventstream from eventstore. commandId:%s, aggregateRootId:%s, aggregateRootTypeName:%s",
                                eventStream.commandId(),
                                eventStream.aggregateRootId(),
                                eventStream.aggregateRootTypeName());
                        _logger.error(errorMessage);
                        resetCommandMailBoxConsumingSequence(context, context.getProcessingCommand().getSequence() + 1);
                        CommandResult commandResult = new CommandResult(CommandStatus.Failed, context.getProcessingCommand().getMessage().id(), eventStream.aggregateRootId(), "Duplicate aggregate creation, but we cannot find the existing eventstream from eventstore.", String.class.getName());
                        completeCommand(context.getProcessingCommand(), commandResult);
                    }
                },
                () -> String.format("[eventStream:%s]", eventStream),
                errorMessage -> _logger.error(String.format("Find the first version of event has unknown exception, the code should not be run to here, errorMessage: %s", errorMessage)),
                true);
    }

    private void refreshAggregateMemoryCache(EventCommittingContext context) {
        try {
            context.getAggregateRoot().acceptChanges(context.getEventStream().version());
            _memoryCache.set(context.getAggregateRoot());
        } catch (Exception ex) {
            _logger.error(String.format("Refresh aggregate memory cache failed for event stream:%s", context.getEventStream()), ex);
        }
    }

    private void refreshAggregateMemoryCacheToLatestVersion(String aggregateRootTypeName, String aggregateRootId) {
        try {
            _memoryCache.refreshAggregateFromEventStore(aggregateRootTypeName, aggregateRootId);
        } catch (Exception ex) {
            _logger.error(String.format("Refresh aggregate memory cache to latest version has unknown exception, aggregateRootTypeName: %s, aggregateRootId:%s", aggregateRootTypeName, aggregateRootId), ex);
        }
    }

    private void publishDomainEventAsyncRecursively(ProcessingCommand processingCommand, DomainEventStreamMessage eventStream) {
        _ioHelper.tryAsyncActionRecursively("PublishDomainEventAsync",
                () -> _domainEventPublisher.publishAsync(eventStream),
                result ->
                {
                    _logger.debug("Publish domain events success, {}", eventStream);

                    String commandHandleResult = processingCommand.getCommandExecuteContext().getResult();
                    CommandResult commandResult = new CommandResult(CommandStatus.Success, processingCommand.getMessage().id(), eventStream.aggregateRootId(), commandHandleResult, String.class.getName());
                    completeCommand(processingCommand, commandResult);
                },
                () -> String.format("[eventStream:%s]", eventStream),
                errorMessage -> _logger.error(String.format("Publish event has unknown exception, the code should not be run to here, errorMessage: %s", errorMessage)),
                true);
    }

    private void ConcatContexts(List<EventCommittingContext> contextList) {
        for (int i = 0; i < contextList.size() - 1; i++) {
            EventCommittingContext currentContext = contextList.get(i);
            EventCommittingContext nextContext = contextList.get(i + 1);
            currentContext.setNext(nextContext);
        }
    }

    private void completeCommand(ProcessingCommand processingCommand, CommandResult commandResult) {
        processingCommand.getMailbox().completeMessage(processingCommand, commandResult);
    }

    private void cleanInactiveMailbox() {
        List<Map.Entry<String, EventMailBox>> inactiveList = _mailboxDict.entrySet().stream().filter(entry ->
                entry.getValue().isInactive(_timeoutSeconds) && entry.getValue().isRunning()
        ).collect(Collectors.toList());

        inactiveList.stream().forEach(entry -> {
            if (_mailboxDict.remove(entry.getKey()) != null) {
                _logger.info("Removed inactive event mailbox, aggregateRootId: {}", entry.getKey());
            }
        });
    }
}
