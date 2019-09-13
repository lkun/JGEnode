package com.kunlv.ddd.j.enode.remoting;

import com.kunlv.ddd.j.enode.remoting.exception.RemotingCommandException;

public interface CommandCustomHeader {
    void checkFields() throws RemotingCommandException;
}
