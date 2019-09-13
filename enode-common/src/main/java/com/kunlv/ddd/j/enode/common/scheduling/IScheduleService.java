package com.kunlv.ddd.j.enode.common.scheduling;

import com.kunlv.ddd.j.enode.common.function.Action;

public interface IScheduleService {
    void startTask(String name, Action action, int dueTime, int period);

    void stopTask(String name);
}
