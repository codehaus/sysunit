package org.sysunit.transport.socket;

import org.sysunit.command.Command;

import java.io.Serializable;
import java.net.InetAddress;

public class SocketMessage
    implements Serializable {

    private InetAddress replyToAddr;
    private int replyToPort;
    private Object payload;

    public SocketMessage(SocketDispatcher replyTo,
                         Object payload) {
        this.replyToAddr = replyTo.getCommandThread().getAddr();
        this.replyToPort = replyTo.getCommandThread().getPort();
        this.payload     = payload;
    }

    public InetAddress getReplyToAddr() {
        return this.replyToAddr;
    }

    public int getReplyToPort() {
        return this.replyToPort;
    }

    public Object getPayload() {
        return this.payload;
    }

    public String toString() {
        return "[SocketMessage: replyToAddr=" + this.replyToAddr
            + "; replyToPort=" + this.replyToPort
            + "; payload=" + this.payload
            + "]";
    }
}
