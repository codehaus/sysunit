package org.sysunit.transport.socket;

import java.net.InetAddress;

public class SlaveNodeInfo {

    public static final SlaveNodeInfo[] EMPTY_ARRAY = new SlaveNodeInfo[0];

    private InetAddress addr;
    private int port;
    private String type;

    public SlaveNodeInfo(InetAddress addr,
                         int port,
                         String type) {
        this.addr = addr;
        this.port = port;
        this.type = type;
    }

    public InetAddress getAddr() {
        return this.addr;
    }

    public int getPort() {
        return this.port;
    }

    public String getType() {
        return this.type;
    }

    public int hashCode() {
        return getAddr().hashCode() + getPort();
    }

    public boolean equals(Object thatObj) {
        if ( thatObj instanceof SlaveNodeInfo ) {
            return ( ((SlaveNodeInfo)thatObj).getAddr().equals( getAddr() )
                     &&
                     ((SlaveNodeInfo)thatObj).getPort() == getPort() );
        }
        
        return false;
    }

    public String toString() {
        return "[SlaveNodeInfo: addr=" + getAddr() + "; port=" + getPort() + "]";
    }
}
