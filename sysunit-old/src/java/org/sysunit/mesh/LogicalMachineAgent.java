package org.sysunit.mesh;

import org.sysunit.mesh.transport.Transport;
import org.sysunit.model.LogicalMachineInfo;

public class LogicalMachineAgent
    implements Dispatcher {

    public static final LogicalMachineAgent[] EMPTY_ARRAY = new LogicalMachineAgent[0];

    private Transport transport;
    private LogicalMachineInfo logicalMachineInfo;

    public LogicalMachineAgent(Transport transport,
                               LogicalMachineInfo logicalMachineInfo) {
        this.transport          = transport;
        this.logicalMachineInfo = logicalMachineInfo;
    }

    protected Transport getTransport() {
        return this.transport;
    }

    public LogicalMachineInfo getInfo() {
        return this.logicalMachineInfo;
    }

    public void dispatch(NodeCommand command)
        throws Exception {
        getTransport().send( null,
                             command );
    }

    public boolean isDone() {
        return false;
    }

    public void kill() {

    }
}

