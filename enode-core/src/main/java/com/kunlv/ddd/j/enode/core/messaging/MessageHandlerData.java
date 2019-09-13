package com.kunlv.ddd.j.enode.core.messaging;

import com.kunlv.ddd.j.enode.core.infrastructure.IObjectProxy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lvk618@gmail.com
 */
public class MessageHandlerData<T extends IObjectProxy> {
    public List<T> allHandlers = new ArrayList<>();
    public List<T> listHandlers = new ArrayList<>();
    public List<T> queuedHandlers = new ArrayList<>();
}
