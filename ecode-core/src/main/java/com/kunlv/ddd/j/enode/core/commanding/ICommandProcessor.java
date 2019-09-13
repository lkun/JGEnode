package com.kunlv.ddd.j.enode.core.commanding;

public interface ICommandProcessor {
    void process(ProcessingCommand processingCommand);

    void start();

    void stop();
}
