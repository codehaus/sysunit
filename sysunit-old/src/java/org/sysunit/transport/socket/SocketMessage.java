package org.sysunit.transport.socket;

import org.sysunit.command.Command;

import java.io.Serializable;
import java.net.InetAddress;

public class SocketMessage
    implements Serializable {

    private int replyToPort;
    private Object payload;

    public SocketMessage(SocketDispatcher replyTo,
                         Object payload) {
        this.replyToPort = replyTo.getCommandThread().getPort();
        this.payload     = payload;
    }

    public int getReplyToPort() {
        return this.replyToPort;
    }

    public Object getPayload() {
        return this.payload;
    }

    public String toString() {
        return "[SocketMessage: replyToPort=" + this.replyToPort
            + "; payload=" + this.payload
            + "]";
    }
}
