package com.kunlv.ddd.j.enode.core;

import com.kunlv.ddd.j.enode.core.commanding.impl.CommandAsyncHandlerProxy;
import com.kunlv.ddd.j.enode.core.commanding.impl.CommandHandlerProxy;
import com.kunlv.ddd.j.enode.core.commanding.impl.DefaultCommandAsyncHandlerProvider;
import com.kunlv.ddd.j.enode.core.commanding.impl.DefaultCommandHandlerProvider;
import com.kunlv.ddd.j.enode.common.container.SpringObjectContainer;
import com.kunlv.ddd.j.enode.common.scheduling.ScheduleService;
import com.kunlv.ddd.j.enode.core.domain.impl.DefaultAggregateRepositoryProvider;
import com.kunlv.ddd.j.enode.core.domain.impl.DefaultAggregateRootFactory;
import com.kunlv.ddd.j.enode.core.domain.impl.DefaultAggregateRootInternalHandlerProvider;
import com.kunlv.ddd.j.enode.core.domain.impl.DefaultAggregateSnapshotter;
import com.kunlv.ddd.j.enode.core.domain.impl.DefaultMemoryCache;
import com.kunlv.ddd.j.enode.core.domain.impl.DefaultRepository;
import com.kunlv.ddd.j.enode.core.domain.impl.EventSourcingAggregateStorage;
import com.kunlv.ddd.j.enode.core.eventing.impl.DefaultEventSerializer;
import com.kunlv.ddd.j.enode.core.eventing.impl.DefaultProcessingEventProcessor;
import com.kunlv.ddd.j.enode.core.infrastructure.impl.DefaultTypeNameProvider;
import com.kunlv.ddd.j.enode.core.messaging.impl.DefaultMessageDispatcher;
import com.kunlv.ddd.j.enode.core.messaging.impl.DefaultMessageHandlerProvider;
import com.kunlv.ddd.j.enode.core.messaging.impl.DefaultThreeMessageHandlerProvider;
import com.kunlv.ddd.j.enode.core.messaging.impl.DefaultTwoMessageHandlerProvider;
import com.kunlv.ddd.j.enode.core.messaging.impl.MessageHandlerProxy1;
import com.kunlv.ddd.j.enode.core.messaging.impl.MessageHandlerProxy2;
import com.kunlv.ddd.j.enode.core.messaging.impl.MessageHandlerProxy3;
import com.kunlv.ddd.j.enode.core.queue.SendReplyService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

/**
 * @author lvk618@gmail.com
 */
public class ENodeAutoConfiguration {
    @Bean
    public ScheduleService scheduleService() {
        return new ScheduleService();
    }

    @Bean
    public DefaultTypeNameProvider defaultTypeNameProvider() {
        return new DefaultTypeNameProvider();
    }

    @Bean
    public SpringObjectContainer springObjectContainer() {
        SpringObjectContainer objectContainer = new SpringObjectContainer();
        ObjectContainer.container = objectContainer;
        return objectContainer;
    }

    @Bean
    public DefaultProcessingEventProcessor defaultProcessingDomainEventStreamMessageProcessor() {
        return new DefaultProcessingEventProcessor();
    }

    /**
     * 原型模式获取bean，每次新建代理执行
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public CommandHandlerProxy commandHandlerProxy() {
        return new CommandHandlerProxy();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public CommandAsyncHandlerProxy commandAsyncHandlerProxy() {
        return new CommandAsyncHandlerProxy();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public MessageHandlerProxy1 messageHandlerProxy1() {
        return new MessageHandlerProxy1();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public MessageHandlerProxy2 messageHandlerProxy2() {
        return new MessageHandlerProxy2();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public MessageHandlerProxy3 messageHandlerProxy3() {
        return new MessageHandlerProxy3();
    }

    @Bean
    public DefaultEventSerializer defaultEventSerializer() {
        return new DefaultEventSerializer();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public SendReplyService sendReplyService() {
        return new SendReplyService();
    }

    @Bean
    public DefaultAggregateRootInternalHandlerProvider aggregateRootInternalHandlerProvider() {
        return new DefaultAggregateRootInternalHandlerProvider();
    }

    @Bean
    public DefaultMessageDispatcher defaultMessageDispatcher() {
        return new DefaultMessageDispatcher();
    }

    @Bean
    public DefaultRepository defaultRepository() {
        return new DefaultRepository();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public DefaultMemoryCache defaultMemoryCache() {
        return new DefaultMemoryCache();
    }

    @Bean
    public DefaultAggregateRepositoryProvider aggregateRepositoryProvider() {
        return new DefaultAggregateRepositoryProvider();
    }

    @Bean
    public DefaultThreeMessageHandlerProvider threeMessageHandlerProvider() {
        return new DefaultThreeMessageHandlerProvider();
    }

    @Bean
    public DefaultTwoMessageHandlerProvider twoMessageHandlerProvider() {
        return new DefaultTwoMessageHandlerProvider();
    }

    @Bean
    public DefaultMessageHandlerProvider messageHandlerProvider() {
        return new DefaultMessageHandlerProvider();
    }

    @Bean
    public DefaultCommandAsyncHandlerProvider commandAsyncHandlerProvider() {
        return new DefaultCommandAsyncHandlerProvider();
    }

    @Bean
    public DefaultCommandHandlerProvider commandHandlerProvider() {
        return new DefaultCommandHandlerProvider();
    }

    @Bean
    public DefaultAggregateRootFactory aggregateRootFactory() {
        return new DefaultAggregateRootFactory();
    }

    @Bean
    public DefaultAggregateSnapshotter aggregateSnapshotter() {
        return new DefaultAggregateSnapshotter();
    }

    @Bean
    public EventSourcingAggregateStorage eventSourcingAggregateStorage() {
        return new EventSourcingAggregateStorage();
    }
}
