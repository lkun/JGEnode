package com.kunlv.ddd.j.enode.example.consumer.eventhandles;

import com.google.common.collect.Lists;
import com.kunlv.ddd.j.enode.core.ENodeBootstrap;
import com.kunlv.ddd.j.enode.core.eventing.impl.InMemoryEventStore;
import com.kunlv.ddd.j.enode.core.eventing.impl.InMemoryPublishedVersionStore;
import com.kunlv.ddd.j.enode.core.queue.command.CommandResultProcessor;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.kunlv.ddd.j.enode.example.QueueProperties.JDBC_URL;

@Configuration
public class EventAppConfig {
    @Bean(initMethod = "init")
    public ENodeBootstrap eNodeBootstrap() {
        ENodeBootstrap bootstrap = new ENodeBootstrap();
        bootstrap.setPackages(Lists.newArrayList("com.kunlv.ddd.j.enode.example"));
        return bootstrap;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public CommandResultProcessor commandResultProcessor() {
        CommandResultProcessor processor = new CommandResultProcessor(6001);
        return processor;
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
//        MysqlEventStore mysqlEventStore = new MysqlEventStore(dataSource, null);
//        return mysqlEventStore;
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