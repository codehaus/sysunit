package org.sysunit.testmesh.slave;

public class PerformSetUpCommand
    extends PerformCommand
{
    public void execute(SlaveNode node)
        throws Exception
    {
        node.performSetUp();
    }
}
