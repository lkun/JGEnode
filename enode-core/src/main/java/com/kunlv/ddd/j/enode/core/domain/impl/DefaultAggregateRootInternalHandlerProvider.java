package com.kunlv.ddd.j.enode.core.domain.impl;

import com.kunlv.ddd.j.enode.common.SysProperties;
import com.kunlv.ddd.j.enode.common.exception.ENodeRuntimeException;
import com.kunlv.ddd.j.enode.common.function.Action2;
import com.kunlv.ddd.j.enode.core.domain.IAggregateRoot;
import com.kunlv.ddd.j.enode.core.domain.IAggregateRootInternalHandlerProvider;
import com.kunlv.ddd.j.enode.core.eventing.IDomainEvent;
import com.kunlv.ddd.j.enode.core.infrastructure.IAssemblyInitializer;
import com.kunlv.ddd.j.enode.core.infrastructure.TypeUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author lvk618@gmail.com
 */
public class DefaultAggregateRootInternalHandlerProvider implements IAggregateRootInternalHandlerProvider, IAssemblyInitializer {
    private Map<Class, Map<Class, Action2<IAggregateRoot, IDomainEvent>>> mappings = new HashMap<>();

    @Override
    public void initialize(Set<Class<?>> componentTypes) {
        componentTypes.stream().filter(TypeUtils::isAggregateRoot).forEach(this::recurseRegisterInternalHandler);
    }

    private void recurseRegisterInternalHandler(Class aggregateRootType) {
        Class superclass = aggregateRootType.getSuperclass();
        if (!isInterfaceOrObjectClass(superclass)) {
            registerInternalHandlerWithSuperclass(aggregateRootType, superclass);
        }
        register(aggregateRootType, aggregateRootType);
    }

    private void registerInternalHandlerWithSuperclass(Class aggregateRootType, Class parentType) {
        Class superclass = parentType.getSuperclass();
        if (!isInterfaceOrObjectClass(superclass)) {
            registerInternalHandlerWithSuperclass(aggregateRootType, superclass);
        }
        register(aggregateRootType, parentType);
    }

    private boolean isInterfaceOrObjectClass(Class type) {
        return Modifier.isInterface(type.getModifiers()) || type.equals(Object.class);
    }

    private void register(Class aggregateRootType, Class type) {
        Arrays.stream(type.getDeclaredMethods())
                .filter(method -> method.getName().equalsIgnoreCase(SysProperties.AGGREGATE_ROOT_HANDLE_METHOD_NAME)
                        && method.getParameterTypes().length == 1
                        && IDomainEvent.class.isAssignableFrom(method.getParameterTypes()[0]))
                .forEach(method -> registerInternalHandler(aggregateRootType, method.getParameterTypes()[0], method));
    }

    private void registerInternalHandler(Class aggregateRootType, Class eventType, Method method) {
        Map<Class, Action2<IAggregateRoot, IDomainEvent>> eventHandlerDic = mappings.computeIfAbsent(aggregateRootType, k -> new HashMap<>());
        method.setAccessible(true);
        try {
            MethodHandle methodHandle = MethodHandles.lookup().unreflect(method);
            eventHandlerDic.put(eventType, (aggregateRoot, domainEvent) -> {
                try {
                    methodHandle.invoke(aggregateRoot, domainEvent);
                } catch (Throwable throwable) {
                    throw new ENodeRuntimeException(throwable);
                }
            });
        } catch (IllegalAccessException e) {
            throw new ENodeRuntimeException(e);
        }
    }

    @Override
    public Action2<IAggregateRoot, IDomainEvent> getInternalEventHandler(Class<? extends IAggregateRoot> aggregateRootType, Class<? extends IDomainEvent> anEventType) {
        Class currentAggregateType = aggregateRootType;
        while (currentAggregateType != null) {
            Action2<IAggregateRoot, IDomainEvent> handler = getEventHandler(currentAggregateType, anEventType);
            if (handler != null) {
                return handler;
            }
            if (currentAggregateType.getSuperclass() != null && Arrays.asList(currentAggregateType.getSuperclass().getInterfaces()).contains(IAggregateRoot.class)) {
                currentAggregateType = currentAggregateType.getSuperclass();
            } else {
                break;
            }
        }
        return null;
    }

    private Action2<IAggregateRoot, IDomainEvent> getEventHandler(Class<? extends IAggregateRoot> aggregateRootType, Class<? extends IDomainEvent> anEventType) {
        Map<Class, Action2<IAggregateRoot, IDomainEvent>> eventHandlerDic = mappings.get(aggregateRootType);
        if (eventHandlerDic == null) {
            return null;
        }
        return eventHandlerDic.get(anEventType);
    }
}