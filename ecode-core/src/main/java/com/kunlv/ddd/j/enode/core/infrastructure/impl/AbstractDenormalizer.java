package com.kunlv.ddd.j.enode.core.infrastructure.impl;

import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.core.infrastructure.messaging.IMessageHandler;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractDenormalizer implements IMessageHandler{
    protected final QueryRunner _queryRunner;

    public AbstractDenormalizer(DataSource denormalizerDatasource) {
        _queryRunner = new QueryRunner(denormalizerDatasource);
    }

    protected CompletableFuture<AsyncTaskResult> executeAsync(String sql, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                _queryRunner.update(sql, params);
                return AsyncTaskResult.Success;
            } catch (SQLException e) {
//                return new AsyncTaskResult(AsyncTaskStatus.Failed, e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }
}
