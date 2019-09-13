package com.kunlv.ddd.j.enode.core.commanding.impl;

import com.kunlv.ddd.j.enode.core.ENode;
import com.kunlv.ddd.j.enode.core.commanding.ICommandProcessor;
import com.kunlv.ddd.j.enode.core.commanding.IProcessingCommandHandler;
import com.kunlv.ddd.j.enode.core.commanding.ProcessingCommand;
import com.kunlv.ddd.j.enode.core.commanding.ProcessingCommandMailbox;
import com.kunlv.ddd.j.enode.common.logging.ENodeLogger;
import com.kunlv.ddd.j.enode.common.scheduling.IScheduleService;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class DefaultCommandProcessor implements ICommandProcessor {
    private static final Logger _logger = ENodeLogger.getLog();

    private final ConcurrentMap<String, ProcessingCommandMailbox> _mailboxDict;
    private final IProcessingCommandHandler _handler;
    private final IScheduleService _scheduleService;
    private final int _timeoutSeconds;
    private final String _taskName;

    @Inject
    public DefaultCommandProcessor(IScheduleService scheduleService, IProcessingCommandHandler handler) {
        _scheduleService = scheduleService;
        _mailboxDict = new ConcurrentHashMap<>();
        _handler = handler;
        _timeoutSeconds = ENode.getInstance().getSetting().getAggregateRootMaxInactiveSeconds();
        _taskName = "CleanInactiveAggregates_" + System.nanoTime() + new Random().nextInt(10000);
    }

    public void process(ProcessingCommand processingCommand) {
        String aggregateRootId = processingCommand.getMessage().getAggregateRootId();
        if (aggregateRootId == null || aggregateRootId.trim().equals("")) {
            throw new IllegalArgumentException("aggregateRootId of command cannot be null or empty, commandId:" + processingCommand.getMessage().id());
        }

        ProcessingCommandMailbox mailbox = _mailboxDict.computeIfAbsent(aggregateRootId, x -> new ProcessingCommandMailbox(x, _handler));
        mailbox.enqueueMessage(processingCommand);
    }

    public void start() {
        _scheduleService.startTask(_taskName, this::cleanInactiveMailbox,
                ENode.getInstance().getSetting().getScanExpiredAggregateIntervalMilliseconds(),
                ENode.getInstance().getSetting().getScanExpiredAggregateIntervalMilliseconds());
    }

    public void stop() {
        _scheduleService.stopTask(_taskName);
    }

    private void cleanInactiveMailbox() {
        List<Map.Entry<String, ProcessingCommandMailbox>> inactiveList = _mailboxDict.entrySet().stream().filter(entry ->
                entry.getValue().isInactive(_timeoutSeconds) && !entry.getValue().isRunning()
        ).collect(Collectors.toList());

        inactiveList.stream().forEach(entry -> {
            if (_mailboxDict.remove(entry.getKey()) != null) {
                _logger.info("Removed inactive command mailbox, aggregateRootId: {}", entry.getKey());
            }
        });
    }
}
