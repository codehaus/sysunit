package org.sysunit.mesh;

import org.sysunit.model.LogicalMachineInfo;
import org.sysunit.util.ProcessRunner;

public class LaunchLogicalMachineCommand
    extends PhysicalMachineCommand {

    private LogicalMachineInfo logicalMachineInfo;

    public LaunchLogicalMachineCommand(String testId,
                                       LogicalMachineInfo logicalMachineInfo) {
        super( testId );
        this.logicalMachineInfo = logicalMachineInfo;
    }

    public LogicalMachineInfo getInfo() {
        return this.logicalMachineInfo;
    }

    public void execute(PhysicalMachine physicalMachine)
        throws Throwable {

        forkLogicalMachine();
        reply( new LogicalMachineLaunchedResponse( getInfo().getId() ) );
    }

    protected void forkLogicalMachine() {
        ProcessRunner runner = ProcessRunner.newJavaProcess( LogicalMachineDaemon.class,
                                                             new String[]{
                                                                 getTestId()
                                                             } );
    }
}
