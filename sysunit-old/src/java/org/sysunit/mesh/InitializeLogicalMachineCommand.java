package org.sysunit.mesh;

import org.sysunit.model.LogicalMachineInfo;

public class InitializeLogicalMachineCommand
    extends LogicalMachineCommand {

    private LogicalMachineInfo logicalMachineInfo;

    public InitializeLogicalMachineCommand(String testId,
                                           LogicalMachineInfo logicalMachineInfo) {
        super( testId );
        this.logicalMachineInfo = logicalMachineInfo;
    }

    public LogicalMachineInfo getInfo() {
        return this.logicalMachineInfo;
    }

    public void execute(LogicalMachine logicalMachine)
        throws Throwable {

        logicalMachine.initialize( getInfo() );

        reply( new LogicalMachineInitializedResponse( getInfo().getId() ) );
    }
}
