package com.kunlv.ddd.j.enode.example.consumer.commandhandles;

import com.google.common.collect.Lists;

import com.kunlv.ddd.j.enode.core.ENodeBootstrap;
import com.kunlv.ddd.j.enode.core.commanding.impl.DefaultCommandProcessor;
import com.kunlv.ddd.j.enode.core.commanding.impl.DefaultProcessingCommandHandler;
import com.kunlv.ddd.j.enode.core.eventing.impl.DefaultEventCommittingService;
import com.kunlv.ddd.j.enode.core.eventing.impl.InMemoryEventStore;
import com.kunlv.ddd.j.enode.core.eventing.impl.InMemoryPublishedVersionStore;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.kunlv.ddd.j.enode.example.QueueProperties.JDBC_URL;

@Configuration
public class CommandConsumerAppConfig {
    /**
     * 命令处理器
     */
    @Bean
    public DefaultProcessingCommandHandler defaultProcessingCommandHandler() {
        return new DefaultProcessingCommandHandler();
    }

    @Bean
    public DefaultEventCommittingService defaultEventService() {
        return new DefaultEventCommittingService();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public DefaultCommandProcessor defaultCommandProcessor() {
        return new DefaultCommandProcessor();
    }

    @Bean(initMethod = "init")
    public ENodeBootstrap eNodeBootstrap() {
        ENodeBootstrap bootstrap = new ENodeBootstrap();
        bootstrap.setPackages(Lists.newArrayList("com.kunlv.ddd.j.enode.example"));
        return bootstrap;
    }

    @Bean
    public InMemoryEventStore inMemoryEventStore() {
        return new InMemoryEventStore();
    }

    @Bean
    public InMemoryPublishedVersionStore inMemoryPublishedVersionStore() {
        return new InMemoryPublishedVersionStore();
    }

//    @Bean
//    public MysqlEventStore mysqlEventStore(HikariDataSource dataSource) {
//        return new MysqlEventStore(dataSource, null);
//    }
//
//    @Bean
//    public MysqlPublishedVersionStore mysqlPublishedVersionStore(HikariDataSource dataSource) {
//        return new MysqlPublishedVersionStore(dataSource, null);
//    }

    @Bean
    public HikariDataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(JDBC_URL);
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setDriverClassName(com.mysql.cj.jdbc.Driver.class.getName());
        return dataSource;
    }
}