package com.kunlv.ddd.j.enode.example.web;

import com.google.common.collect.Lists;
import com.kunlv.ddd.j.enode.core.ENodeBootstrap;
import com.kunlv.ddd.j.enode.core.eventing.impl.InMemoryEventStore;
import com.kunlv.ddd.j.enode.core.eventing.impl.InMemoryPublishedVersionStore;
import com.kunlv.ddd.j.enode.core.queue.command.CommandResultProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfigCommand {
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public CommandResultProcessor commandResultProcessor() {
        CommandResultProcessor processor = new CommandResultProcessor();
        return processor;
    }

    @Bean(initMethod = "init")
    public ENodeBootstrap eNodeBootstrap() {
        ENodeBootstrap bootstrap = new ENodeBootstrap();
        bootstrap.setPackages(Lists.newArrayList("com.kunlv.ddd.j.enode.example"));
        return bootstrap;
    }

    @Bean
    public InMemoryPublishedVersionStore versionStore() {
        return new InMemoryPublishedVersionStore();
    }

    @Bean
    public InMemoryEventStore eventStore() {
        return new InMemoryEventStore();
    }
}
