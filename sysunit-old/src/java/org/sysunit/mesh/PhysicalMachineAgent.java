package org.sysunit.mesh;

import org.sysunit.mesh.transport.Transport;
import org.sysunit.model.LogicalMachineInfo;

import java.util.List;
import java.util.ArrayList;

public class PhysicalMachineAgent
    implements Dispatcher {

    public static final PhysicalMachineAgent[] EMPTY_ARRAY = new PhysicalMachineAgent[0];

    private Transport transport;
    private String id;

    public PhysicalMachineAgent(Transport transport,
                                String id) {
        this.transport       = transport;
        this.id              = id;
    }

    protected Transport getTransport() {
        return this.transport;
    }

    public String getId() {
        return this.id;
    }

    public void launchLogicalMachine(String testId,
                                     LogicalMachineInfo logicalMachineInfo)
        throws Exception {

        LaunchLogicalMachineCommand command = new LaunchLogicalMachineCommand( testId,
                                                                               logicalMachineInfo );

        dispatch( command );
    }

    public void dispatch(NodeCommand command)
        throws Exception {
        getTransport().send( getId(),
                             command );
    }
}
