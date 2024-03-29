package com.kunlv.ddd.j.enode.core.eventing.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import  com.kunlv.ddd.j.enode.core.ENode;
import  com.kunlv.ddd.j.enode.common.container.IObjectContainer;
import  com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import  com.kunlv.ddd.j.enode.common.io.AsyncTaskStatus;
import  com.kunlv.ddd.j.enode.common.io.IOHelper;
import  com.kunlv.ddd.j.enode.common.io.IORuntimeException;
import  com.kunlv.ddd.j.enode.common.logging.ENodeLogger;
import  com.kunlv.ddd.j.enode.common.serializing.IJsonSerializer;
import  com.kunlv.ddd.j.enode.common.utilities.Ensure;
import  com.kunlv.ddd.j.enode.core.configurations.DefaultDBConfigurationSetting;
import  com.kunlv.ddd.j.enode.core.configurations.OptionSetting;
import  com.kunlv.ddd.j.enode.core.eventing.*;
import  com.kunlv.ddd.j.enode.core.infrastructure.WrappedRuntimeException;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MysqlEventStore implements IEventStore {
    private static final Logger _logger = ENodeLogger.getLog();

    private static final String EventTableNameFormat = "{0}_{1}";

    private final String _tableName;
    private final int _tableCount;
    private final String _versionIndexName;
    private final String _commandIndexName;
    private final int _bulkCopyBatchSize;
    private final int _bulkCopyTimeout;
    private final IJsonSerializer _jsonSerializer;
    private final IEventSerializer _eventSerializer;
    private final IOHelper _ioHelper;
    private final QueryRunner _queryRunner;
    private boolean _supportBatchAppendEvent;
    private Executor executor;

    public MysqlEventStore(DataSource ds, OptionSetting optionSetting, IObjectContainer objectContainer) {
        Ensure.notNull(ds, "ds");
        if (optionSetting != null) {
            _tableName = optionSetting.getOptionValue("TableName");
            _tableCount = optionSetting.getOptionValue("TableCount") == null ? 1 : Integer.valueOf(optionSetting.getOptionValue("TableCount"));
            _versionIndexName = optionSetting.getOptionValue("VersionIndexName");
            _commandIndexName = optionSetting.getOptionValue("CommandIndexName");
            _bulkCopyBatchSize = optionSetting.getOptionValue("BulkCopyBatchSize") == null ? 0 : Integer.valueOf(optionSetting.getOptionValue("BulkCopyBatchSize"));
            _bulkCopyTimeout = optionSetting.getOptionValue("BulkCopyTimeout") == null ? 0 : Integer.valueOf(optionSetting.getOptionValue("BulkCopyTimeout"));

        } else {
            DefaultDBConfigurationSetting setting = ENode.getInstance().getSetting().getDefaultDBConfigurationSetting();
            _tableName = setting.getEventTableName();
            _tableCount = setting.getEventTableCount();
            _versionIndexName = setting.getEventTableVersionUniqueIndexName();
            _commandIndexName = setting.getEventTableCommandIdUniqueIndexName();
            _bulkCopyBatchSize = setting.getEventTableBulkCopyBatchSize();
            _bulkCopyTimeout = setting.getEventTableBulkCopyTimeout();
        }

        Ensure.notNull(_tableName, "_tableName");
        Ensure.notNull(_versionIndexName, "_eventIndexName");
        Ensure.notNull(_commandIndexName, "_commandIndexName");
        Ensure.positive(_bulkCopyBatchSize, "_bulkCopyBatchSize");
        Ensure.positive(_bulkCopyTimeout, "_bulkCopyTimeout");

        _jsonSerializer = objectContainer.resolve(IJsonSerializer.class);
        _eventSerializer = objectContainer.resolve(IEventSerializer.class);
        _ioHelper = objectContainer.resolve(IOHelper.class);
        _queryRunner = new QueryRunner(ds);
        executor = Executors.newFixedThreadPool(4,
                new ThreadFactoryBuilder().setDaemon(true).setNameFormat("MysqlEventStoreExecutor-%d").build());
    }

    public boolean isSupportBatchAppendEvent() {
        return _supportBatchAppendEvent;
    }

    public void setSupportBatchAppendEvent(boolean _supportBatchAppendEvent) {
        this._supportBatchAppendEvent = _supportBatchAppendEvent;
    }

    public List<DomainEventStream> queryAggregateEvents(String aggregateRootId, String aggregateRootTypeName, int minVersion, int maxVersion) {
        List<StreamRecord> records = _ioHelper.tryIOFunc(() -> {
                    try {
                        return _queryRunner.query(String.format("SELECT * FROM `%s` WHERE AggregateRootId = ? AND Version >= ? AND Version <= ? ORDER BY Version", getTableName(aggregateRootId)),
                                new BeanListHandler<>(StreamRecord.class),
                                aggregateRootId,
                                minVersion,
                                maxVersion);
                    } catch (SQLException ex) {
                        String errorMessage = String.format("Failed to query aggregate events, aggregateRootId: %s, aggregateRootType: %s", aggregateRootId, aggregateRootTypeName);
                        _logger.error(errorMessage, ex);
                        throw new IORuntimeException(errorMessage, ex);
                    } catch (Exception ex) {
                        String errorMessage = String.format("Failed to query aggregate events, aggregateRootId: %s aggregateRootType: %s", aggregateRootId, aggregateRootTypeName);
                        _logger.error(errorMessage, ex);
                        throw new WrappedRuntimeException(ex);
                    }
                }
                , "QueryAggregateEvents");

        return records.stream().map(this::convertFrom).collect(Collectors.toList());
    }

    public CompletableFuture<AsyncTaskResult<List<DomainEventStream>>> queryAggregateEventsAsync(String aggregateRootId, String aggregateRootTypeName, int minVersion, int maxVersion) {
        return _ioHelper.tryIOFuncAsync(() ->
                        CompletableFuture.supplyAsync(() -> {
                            try {
                                String sql = String.format("SELECT * FROM `%s` WHERE AggregateRootId = ? AND Version >= ? AND Version <= ? ORDER BY Version", getTableName(aggregateRootId));
                                List<StreamRecord> result = _queryRunner.query(sql,
                                        new BeanListHandler<>(StreamRecord.class),
                                        aggregateRootId,
                                        minVersion,
                                        maxVersion);

                                List<DomainEventStream> streams = result.stream().map(this::convertFrom).collect(Collectors.toList());
                                return new AsyncTaskResult<>(AsyncTaskStatus.Success, streams);
                            } catch (SQLException ex) {
                                String errorMessage = String.format("Failed to query aggregate events async, aggregateRootId: %s, aggregateRootType: %s", aggregateRootId, aggregateRootTypeName);
                                _logger.error(errorMessage, ex);
                                return new AsyncTaskResult<>(AsyncTaskStatus.IOException, ex.getMessage());
                            } catch (Exception ex) {
                                String errorMessage = String.format("Failed to query aggregate events async, aggregateRootId: %s, aggregateRootType: %s", aggregateRootId, aggregateRootTypeName);
                                _logger.error(errorMessage, ex);
                                return new AsyncTaskResult<>(AsyncTaskStatus.Failed, ex.getMessage());
                            }
                        }, executor)
                , "QueryAggregateEventsAsync");
    }

    @Override
    public AsyncTaskResult<EventAppendResult> batchAppend(List<DomainEventStream> eventStreams) {
        if (eventStreams.size() == 0) {
            throw new IllegalArgumentException("Event streams cannot be empty.");
        }

        List<String> aggregateRootIds = eventStreams.stream().map(x -> x.aggregateRootId()).distinct().collect(Collectors.toList());
        if (aggregateRootIds.size() > 1) {
            throw new IllegalArgumentException("Batch append event only support for one aggregate.");
        }

        String aggregateRootId = aggregateRootIds.get(0);

        Object[][] params = new Object[eventStreams.size()][];

        for (int i = 0, len = eventStreams.size(); i < len; i++) {
            DomainEventStream eventStream = eventStreams.get(i);
            params[i] = new Object[]{eventStream.aggregateRootId(), eventStream.aggregateRootTypeName(), eventStream.commandId(), eventStream.version(), eventStream.timestamp(),
                    _jsonSerializer.serialize(_eventSerializer.serialize(eventStream.events()))};
        }

        try {
            _queryRunner.batch(String.format("INSERT INTO %s(AggregateRootId,AggregateRootTypeName,CommandId,Version,CreatedOn,Events) VALUES(?,?,?,?,?,?)", getTableName(aggregateRootId)), params);
            return new AsyncTaskResult<>(AsyncTaskStatus.Success, EventAppendResult.Success);
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062 && ex.getMessage().contains(_versionIndexName)) {
                return new AsyncTaskResult<>(AsyncTaskStatus.Success, EventAppendResult.DuplicateEvent);
            } else if (ex.getErrorCode() == 1062 && ex.getMessage().contains(_commandIndexName)) {
                return new AsyncTaskResult<>(AsyncTaskStatus.Success, EventAppendResult.DuplicateCommand);
            }
            _logger.error("Batch append event has sql exception.", ex);
            return new AsyncTaskResult<>(AsyncTaskStatus.IOException, ex.getMessage(), EventAppendResult.Failed);
        } catch (Exception ex) {
            _logger.error("Batch append event has unknown exception.", ex);
            return new AsyncTaskResult<>(AsyncTaskStatus.Failed, ex.getMessage(), EventAppendResult.Failed);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.complete("test");

        CompletableFuture<String> future1 = future.thenApply(v -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(v);
            return "1" + v;
        });

        CompletableFuture<String> future2 = future.thenApply(v -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(v);
            return "2" + v;
        });

        future1.handleAsync((r,e)->{
            System.out.println(r);
            return null;
        });

        future2.handleAsync((r,e)->{
            System.out.println(r);
            return null;
        });

        Thread.sleep(20000);
    }

    @Override
    public AsyncTaskResult<EventAppendResult> append(DomainEventStream eventStream) {
        return _ioHelper.tryIOFunc(()->doAppend(eventStream),"AppendEvents");
    }

    private AsyncTaskResult<EventAppendResult> doAppend(final DomainEventStream eventStream) {
        StreamRecord record = convertTo(eventStream);
        try {
            _queryRunner.update(String.format("INSERT INTO %s(AggregateRootId,AggregateRootTypeName,CommandId,Version,CreatedOn,Events) VALUES(?,?,?,?,?,?)", getTableName(record.getAggregateRootId())),
                    record.getAggregateRootId(),
                    record.getAggregateRootTypeName(),
                    record.getCommandId(),
                    record.getVersion(),
                    record.getCreatedOn(),
                    record.getEvents());
            return new AsyncTaskResult<>(AsyncTaskStatus.Success, EventAppendResult.Success);
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062 && ex.getMessage().contains(_versionIndexName)) {
                return new AsyncTaskResult<>(AsyncTaskStatus.Success, EventAppendResult.DuplicateEvent);
            } else if (ex.getErrorCode() == 1062 && ex.getMessage().contains(_commandIndexName)) {
                return new AsyncTaskResult<>(AsyncTaskStatus.Success, EventAppendResult.DuplicateCommand);
            }

            _logger.error(String.format("Append event has sql exception, eventStream: %s", eventStream), ex);
            return new AsyncTaskResult<>(AsyncTaskStatus.IOException, ex.getMessage(), EventAppendResult.Failed);
        } catch (Exception ex) {
            _logger.error(String.format("Append event has unknown exception, eventStream: %s", eventStream), ex);
            return new AsyncTaskResult<>(AsyncTaskStatus.Failed, ex.getMessage(), EventAppendResult.Failed);
        }
    }

    public CompletableFuture<AsyncTaskResult<DomainEventStream>> findAsync(String aggregateRootId, int version) {
        return _ioHelper.tryIOFuncAsync(() ->
                        CompletableFuture.supplyAsync(() -> {
                            try {
                                StreamRecord record = _queryRunner.query(String.format("select * from `%s` where AggregateRootId=? and Version=?", getTableName(aggregateRootId)),
                                        new BeanHandler<>(StreamRecord.class),
                                        aggregateRootId,
                                        version);

                                DomainEventStream stream = record != null ? convertFrom(record) : null;

                                return new AsyncTaskResult<>(AsyncTaskStatus.Success, stream);
                            } catch (SQLException ex) {
                                _logger.error(String.format("Find event by version has sql exception, aggregateRootId: %s, version: %d", aggregateRootId, version), ex);
                                return new AsyncTaskResult<>(AsyncTaskStatus.IOException, ex.getMessage());
                            } catch (Exception ex) {
                                _logger.error(String.format("Find event by version has unknown exception, aggregateRootId: %s, version: %d", aggregateRootId, version), ex);
                                return new AsyncTaskResult<>(AsyncTaskStatus.Failed, ex.getMessage());
                            }
                        }, executor)
                , "FindEventByVersionAsync");
    }

    public CompletableFuture<AsyncTaskResult<DomainEventStream>> findAsync(String aggregateRootId, String commandId) {
        return _ioHelper.tryIOFuncAsync(() ->
                        CompletableFuture.supplyAsync(() -> {
                            try {

                                StreamRecord record = _queryRunner.query(String.format("select * from `%s` where AggregateRootId=? and CommandId=?", getTableName(aggregateRootId)),
                                        new BeanHandler<>(StreamRecord.class),
                                        aggregateRootId,
                                        commandId);

                                DomainEventStream stream = record != null ? convertFrom(record) : null;
                                return new AsyncTaskResult<>(AsyncTaskStatus.Success, stream);
                            } catch (SQLException ex) {
                                _logger.error(String.format("Find event by commandId has sql exception, aggregateRootId: %s, commandId: %s", aggregateRootId, commandId), ex);
                                return new AsyncTaskResult<>(AsyncTaskStatus.IOException, ex.getMessage());
                            } catch (Exception ex) {
                                _logger.error(String.format("Find event by commandId has unknown exception, aggregateRootId: %s, commandId: %s", aggregateRootId, commandId), ex);
                                return new AsyncTaskResult<>(AsyncTaskStatus.Failed, ex.getMessage());
                            }
                        }, executor)
                , "FindEventByCommandIdAsync");
    }

    private int getTableIndex(String aggregateRootId) {
        int hash = aggregateRootId.hashCode();
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash % _tableCount;
    }

    private String getTableName(String aggregateRootId) {
        if (_tableCount <= 1) {
            return _tableName;
        }

        int tableIndex = getTableIndex(aggregateRootId);

        return String.format(EventTableNameFormat, _tableName, tableIndex);
    }

    private DomainEventStream convertFrom(StreamRecord record) {
        return new DomainEventStream(
                record.getCommandId(),
                record.getAggregateRootId(),
                record.getAggregateRootTypeName(),
                record.getVersion(),
                record.getCreatedOn(),
                _eventSerializer.deserialize(_jsonSerializer.deserialize(record.getEvents(), Map.class), IDomainEvent.class),
                null);
    }

    private StreamRecord convertTo(DomainEventStream eventStream) {
        return new StreamRecord(eventStream.commandId(), eventStream.aggregateRootId(), eventStream.aggregateRootTypeName(),
                eventStream.version(), eventStream.timestamp(),
                _jsonSerializer.serialize(_eventSerializer.serialize(eventStream.events())));
    }
}
