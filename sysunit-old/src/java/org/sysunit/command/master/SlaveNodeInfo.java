package org.sysunit.command.master;

import org.sysunit.command.Dispatcher;

public class SlaveNodeInfo {

    public static final SlaveNodeInfo[] EMPTY_ARRAY = new SlaveNodeInfo[0];

    private String slaveNodeName;
    private Dispatcher dispatcher;

    public SlaveNodeInfo(String slaveNodeName,
                         Dispatcher dispatcher) {
        this.slaveNodeName = slaveNodeName;
        this.dispatcher = dispatcher;
    }

    public String getSlaveNodeName() {
        return this.slaveNodeName;
    }

    public Dispatcher getDispatcher() {
        return this.dispatcher;
    }

    public int hashCode() {
        return this.slaveNodeName.hashCode();
    }

    public boolean equals(Object thatObj) {
        if ( thatObj instanceof SlaveNodeInfo ) {
            return ((SlaveNodeInfo)thatObj).slaveNodeName.equals( this.slaveNodeName );
        }

        return false;
    }
}
