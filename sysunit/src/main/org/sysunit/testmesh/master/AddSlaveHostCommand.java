package org.sysunit.testmesh.master;

import org.sysunit.model.PhysicalMachineInfo;

public class AddSlaveHostCommand
    extends MasterCommand
{
    private PhysicalMachineInfo physicalMachineInfo;

    public AddSlaveHostCommand(PhysicalMachineInfo physicalMachineInfo)
    {
        this.physicalMachineInfo = physicalMachineInfo;
    }

    public PhysicalMachineInfo getPhysicalMachineInfo()
    {
        return this.physicalMachineInfo;
    }

    public void execute(MasterNode master)
        throws Exception
    {
        master.addSlaveHost( getOrigin(),
                             getPhysicalMachineInfo() );
    }
}

    
