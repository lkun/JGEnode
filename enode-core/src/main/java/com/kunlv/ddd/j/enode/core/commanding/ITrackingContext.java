package com.kunlv.ddd.j.enode.core.commanding;

import com.kunlv.ddd.j.enode.core.domain.IAggregateRoot;

import java.util.List;

public interface ITrackingContext {
    /**
     * Get all the tracked aggregates.
     */
    List<IAggregateRoot> getTrackedAggregateRoots();

    /**
     * Clear the tracking context.
     */
    void clear();
}
