package org.sysunit.mesh;

import org.sysunit.mesh.transport.Transport;

public abstract class PhysicalMachineCommand
    implements NodeCommand {

    private String testId;

    private Transport transport;

    public PhysicalMachineCommand(String testId) {
        this.testId = testId;
    }

    public String getTestId() {
        return this.testId;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    protected Transport getTransport() {
        return this.transport;
    }

    public void execute(MeshNode node)
        throws Throwable {
        execute( (PhysicalMachine) node );
    }

    public abstract void execute(PhysicalMachine physicalMachine)
        throws Throwable;

    public void reply(MeshManagerCommand reply)
        throws Throwable {
        getTransport().send( getTestId(),
                             reply );
    }
}
