package com.kunlv.ddd.j.enode.core.infrastructure.messaging.impl;

import  com.kunlv.ddd.j.enode.core.ENode;
import  com.kunlv.ddd.j.enode.common.logging.ENodeLogger;
import  com.kunlv.ddd.j.enode.common.scheduling.IScheduleService;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.*;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class DefaultMessageProcessor<X extends IProcessingMessage<X, Y>, Y extends IMessage> implements IMessageProcessor<X, Y> {

    private static final Logger _logger = ENodeLogger.getLog();

    private ConcurrentMap<String, ProcessingMessageMailbox<X, Y>> _mailboxDict;
    private IProcessingMessageScheduler<X, Y> _processingMessageScheduler;
    private IProcessingMessageHandler<X, Y> _processingMessageHandler;
    private final IScheduleService _scheduleService;
    private final int _timeoutSeconds;
    private final String _taskName;

    public DefaultMessageProcessor(IProcessingMessageScheduler<X, Y> processingMessageScheduler,
                                   IProcessingMessageHandler<X, Y> processingMessageHandler,
                                   IScheduleService scheduleService) {
        _mailboxDict = new ConcurrentHashMap<>();
        _processingMessageScheduler = processingMessageScheduler;
        _processingMessageHandler = processingMessageHandler;
        _scheduleService = scheduleService;
        _timeoutSeconds = ENode.getInstance().getSetting().getAggregateRootMaxInactiveSeconds();
        _taskName = "CleanInactiveAggregates_" + System.nanoTime() + new Random().nextInt(10000);
    }

    public String getMessageName() {
        return "message";
    }

    @Override
    public void process(X processingMessage) {
        String routingKey = processingMessage.getMessage().getRoutingKey();
        if (routingKey != null && !routingKey.trim().equals("")) {
//            ProcessingMessageMailbox<X, Y, Z> mailbox = _mailboxDict.putIfAbsent(routingKey, new ProcessingMessageMailbox<>(_processingMessageScheduler, _processingMessageHandler));
            ProcessingMessageMailbox<X, Y> mailbox = _mailboxDict.computeIfAbsent(routingKey, key -> new ProcessingMessageMailbox<>(routingKey, _processingMessageScheduler, _processingMessageHandler));
            mailbox.enqueueMessage(processingMessage);
        } else {
            _processingMessageScheduler.scheduleMessage(processingMessage);
        }
    }

    @Override
    public void start() {
        _scheduleService.startTask(_taskName, this::cleanInactiveMailbox, ENode.getInstance().getSetting().getScanExpiredAggregateIntervalMilliseconds(), ENode.getInstance().getSetting().getScanExpiredAggregateIntervalMilliseconds());
    }

    @Override
    public void stop() {
        _scheduleService.stopTask(_taskName);
    }

    private void cleanInactiveMailbox() {
        List<Map.Entry<String, ProcessingMessageMailbox<X, Y>>> inactiveList = _mailboxDict.entrySet().stream().filter(entry ->
                entry.getValue().isInactive(_timeoutSeconds) && !entry.getValue().isRunning()
        ).collect(Collectors.toList());

        inactiveList.stream().forEach(entry -> {
            if (_mailboxDict.remove(entry.getKey()) != null) {
                _logger.info("Removed inactive {} mailbox, aggregateRootId: {}", getMessageName(), entry.getKey());
            }
        });
    }
}
