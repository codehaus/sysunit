package org.sysunit.mesh;

import org.sysunit.mesh.transport.Transport;

public abstract class LogicalMachineCommand
    implements NodeCommand {

    private String testId;
    private Transport transport;

    public LogicalMachineCommand(String testId) {
        this.testId = testId;
    }

    public String getTestId() {
        return this.testId;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public Transport getTransport() {
        return this.transport;
    }
        
    public void execute(MeshNode node)
        throws Throwable {
        execute( (LogicalMachine) node );
    }

    public abstract void execute(LogicalMachine logicalMachine)
        throws Throwable;

    public void reply(MeshManagerCommand command)
        throws Throwable {
        getTransport().send( getTestId(),
                             command );
    }
}
