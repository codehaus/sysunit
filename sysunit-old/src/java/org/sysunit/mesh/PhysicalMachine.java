package org.sysunit.mesh;

import org.sysunit.mesh.transport.Transport;
import org.sysunit.model.LogicalMachineInfo;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class PhysicalMachine
    extends AbstractMeshNode {

    public static final PhysicalMachine[] EMPTY_ARRAY = new PhysicalMachine[0];

    private String id;

    public PhysicalMachine(Transport transport,
                           String id) {
        super( transport );
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void execute(NodeCommand command)
        throws Throwable {
        execute( (PhysicalMachineCommand) command );
    }

    public void execute(PhysicalMachineCommand command)
        throws Throwable {
        command.setTransport( getTransport() );
        command.execute( this );
    }
}
