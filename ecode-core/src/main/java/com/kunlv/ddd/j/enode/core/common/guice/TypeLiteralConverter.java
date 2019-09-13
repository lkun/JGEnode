package com.kunlv.ddd.j.enode.core.common.guice;

import com.google.inject.TypeLiteral;
import com.kunlv.ddd.j.enode.core.commanding.ICommand;
import com.kunlv.ddd.j.enode.common.container.GenericTypeLiteral;
import com.kunlv.ddd.j.enode.queue.rocketmq.ITopicProvider;

import java.lang.reflect.Type;

public class TypeLiteralConverter {
    public  static <T> TypeLiteral<T> convert(GenericTypeLiteral<T> genericTypeLiteral) {
        Type superclassTypeParameter = genericTypeLiteral.getType();
        return (TypeLiteral<T>)TypeLiteral.get(superclassTypeParameter);
    }

    public static void main(String[] args){
        TypeLiteral<ITopicProvider<ICommand>> t1 = TypeLiteralConverter.convert(new GenericTypeLiteral<ITopicProvider<ICommand>>(){});

        TypeLiteral<ITopicProvider<ICommand>> t2 = new TypeLiteral<ITopicProvider<ICommand>>() {
        };

        System.out.println(t1.getRawType());
        System.out.println(t2.getRawType());
        System.out.println(t2.equals(t1));
    }
}
