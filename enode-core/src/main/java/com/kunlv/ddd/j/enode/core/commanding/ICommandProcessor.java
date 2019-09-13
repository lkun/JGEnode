package com.kunlv.ddd.j.enode.core.commanding;

public interface ICommandProcessor {
    /**
     * Process the given command.
     *
     * @param processingCommand
     */
    void process(ProcessingCommand processingCommand);

    void start();

    void stop();
}
