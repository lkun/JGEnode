package com.kunlv.ddd.j.enode.core.commanding;

import com.kunlv.ddd.j.enode.core.domain.IAggregateRoot;

import java.util.List;

public interface ITrackingContext {
    List<IAggregateRoot> getTrackedAggregateRoots();

    void clear();
}
