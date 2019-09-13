package com.kunlv.ddd.j.enode.core.jmx;

public interface ForkJoinPoolMBean {
    int getQueuedSubmissionCount();
    int getActiveThreadCount();
    int getCommonPoolParallelism();
    int getPoolSize();
    long getQueuedTaskCount();
    int getRunningThreadCount();
    long getStealCount();
}
