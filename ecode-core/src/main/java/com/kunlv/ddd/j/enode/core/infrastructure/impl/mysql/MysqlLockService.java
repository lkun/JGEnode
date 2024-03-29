package com.kunlv.ddd.j.enode.core.infrastructure.impl.mysql;

import  com.kunlv.ddd.j.enode.core.ENode;
import  com.kunlv.ddd.j.enode.common.function.Action;
import  com.kunlv.ddd.j.enode.common.utilities.Ensure;
import  com.kunlv.ddd.j.enode.core.configurations.DefaultDBConfigurationSetting;
import  com.kunlv.ddd.j.enode.core.configurations.OptionSetting;
import  com.kunlv.ddd.j.enode.core.configurations.StringKeyValuePair;
import  com.kunlv.ddd.j.enode.core.infrastructure.ILockService;
import  com.kunlv.ddd.j.enode.core.infrastructure.WrappedRuntimeException;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class MysqlLockService implements ILockService {
    private final String _tableName;
    private final String _lockKeySqlFormat;
    private final DataSource _ds;
    private final QueryRunner _queryRunner;

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.setProperty("driverClassName","com.mysql.jdbc.Driver");
        properties.setProperty("url","jdbc:mysql://wwwjishulink.mysql.rds.aliyuncs.com:3306/jishulink_view_dev");
        properties.setProperty("username","dev");
        properties.setProperty("password","coffee");
        properties.setProperty("initialSize","1");
        properties.setProperty("maxTotal","1");

        BasicDataSource dataSource = BasicDataSourceFactory.createDataSource(properties);

        MysqlLockService lockService = new MysqlLockService(dataSource, new OptionSetting(new StringKeyValuePair("TableName", "LockKey")));
        lockService.addLockKey("test");
    }

    public MysqlLockService(DataSource ds, OptionSetting optionSetting) {
        Ensure.notNull(ds, "ds");

        if (optionSetting != null)
        {
            _tableName = optionSetting.getOptionValue("TableName");
        }
        else
        {
            DefaultDBConfigurationSetting setting = ENode.getInstance().getSetting().getDefaultDBConfigurationSetting();
            _tableName = setting.getLockKeyTableName();
        }

        Ensure.notNull(_tableName, "_tableName");

        _lockKeySqlFormat = "SELECT * FROM `" + _tableName + "` WHERE `Name` = ? FOR UPDATE";

        _ds = ds;
        _queryRunner = new QueryRunner(ds);
    }

    public void addLockKey(String lockKey) {
        try {
//            _queryRunner.update("insert into lockkey(name)values(?)",lockKey);
//            Integer count = _queryRunner.query(String.format("SELECT COUNT(*) FROM %s WHERE NAME=?", _tableName), new BeanHandler<>(Integer.class), lockKey);
            int count = (int)(long)_queryRunner.query(String.format("SELECT COUNT(*) FROM %s WHERE NAME=?", _tableName), new ScalarHandler<>(), lockKey);


            if (count == 0) {
                _queryRunner.update(String.format("INSERT INTO %s VALUES(?)", _tableName), lockKey);
            }
        } catch (SQLException ex) {
            throw new WrappedRuntimeException(ex);
        }
    }

    public void executeInLock(String lockKey, Action action) {

        try (Connection connection = getConnection()) {
            try {
                connection.setAutoCommit(false);
                lockKey(connection, lockKey);
                action.apply();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
            } catch (Exception e) {
                connection.rollback();
            }
        } catch (SQLException ex) {
            throw new WrappedRuntimeException(ex);
        }
    }

    private void lockKey(Connection connection, String key) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(_lockKeySqlFormat);
        statement.setString(1, key);
        statement.executeQuery();
    }

    private Connection getConnection() throws SQLException {
        return _ds.getConnection();
    }
}
