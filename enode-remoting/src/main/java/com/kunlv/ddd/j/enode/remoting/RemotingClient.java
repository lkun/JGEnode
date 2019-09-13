package com.kunlv.ddd.j.enode.remoting;

import com.kunlv.ddd.j.enode.remoting.exception.RemotingConnectException;
import com.kunlv.ddd.j.enode.remoting.exception.RemotingSendRequestException;
import com.kunlv.ddd.j.enode.remoting.exception.RemotingTimeoutException;
import com.kunlv.ddd.j.enode.remoting.exception.RemotingTooMuchRequestException;
import com.kunlv.ddd.j.enode.remoting.netty.NettyRequestProcessor;
import com.kunlv.ddd.j.enode.remoting.protocol.RemotingCommand;

public interface RemotingClient extends RemotingService {
    RemotingCommand invokeSync(final String addr, final RemotingCommand request,
                               final long timeoutMillis) throws InterruptedException, RemotingConnectException,
            RemotingSendRequestException, RemotingTimeoutException;

    void invokeAsync(final String addr, final RemotingCommand request, final long timeoutMillis,
                     final InvokeCallback invokeCallback) throws InterruptedException, RemotingConnectException,
            RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;

    void invokeOneway(final String addr, final RemotingCommand request, final long timeoutMillis)
            throws InterruptedException, RemotingConnectException, RemotingTooMuchRequestException,
            RemotingTimeoutException, RemotingSendRequestException;

    void registerProcessor(final int requestCode, final NettyRequestProcessor processor);

    boolean isChannelWritable(final String addr);
}
