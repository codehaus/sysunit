package org.sysunit.testmesh.slave;

public class PerformStopCommand
    extends PerformCommand
{
    public void execute(SlaveNode node)
        throws Exception
    {
        node.performStop();
    }
}
