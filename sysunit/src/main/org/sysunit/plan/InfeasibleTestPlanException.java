package org.sysunit.plan;

import org.sysunit.SysUnitException;
import org.sysunit.model.DistributedSystemTestInfo;
import org.sysunit.model.JvmInfo;

public class InfeasibleTestPlanException
    extends SysUnitException
{
    private DistributedSystemTestInfo systemTest;
    private JvmInfo[] unsatisfiedJvms;

    public InfeasibleTestPlanException(DistributedSystemTestInfo systemTest,
                                       JvmInfo[] unsatifiedJvms)
    {
        this.systemTest  = systemTest;
        this.unsatisfiedJvms = unsatisfiedJvms;
    }

    public DistributedSystemTestInfo getSystemTest()
    {
        return this.systemTest;
    }

    public JvmInfo[] getUnsatisfiedJvms()
    {
        return this.unsatisfiedJvms;
    }
}
