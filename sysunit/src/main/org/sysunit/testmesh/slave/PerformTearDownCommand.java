package org.sysunit.testmesh.slave;

public class PerformTearDownCommand
    extends PerformCommand
{
    public void execute(SlaveNode node)
        throws Exception
    {
        node.performTearDown();
    }
}
