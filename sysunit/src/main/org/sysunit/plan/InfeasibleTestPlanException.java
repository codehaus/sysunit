package org.sysunit.plan;

import org.sysunit.SysUnitException;
import org.sysunit.model.DistributedSystemTestInfo;
import org.sysunit.model.JvmInfo;

import java.util.Arrays;

public class InfeasibleTestPlanException
    extends SysUnitException
{
    private DistributedSystemTestInfo systemTest;
    private JvmInfo[] unsatisfiedJvms;

    public InfeasibleTestPlanException(DistributedSystemTestInfo systemTest,
                                       JvmInfo[] unsatisfiedJvms)
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

    public String getMessage()
    {
        return "No slaves to satisfy " + Arrays.asList( this.unsatisfiedJvms );
    }
}
