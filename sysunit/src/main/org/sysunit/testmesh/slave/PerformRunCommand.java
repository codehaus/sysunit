package org.sysunit.testmesh.slave;

public class PerformRunCommand
    extends PerformCommand
{
    public void execute(SlaveNode node)
        throws Exception
    {
        node.performRun();
    }
}
