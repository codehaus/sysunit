package org.sysunit.testmesh.slave;

public class PerformAssertValidCommand
    extends PerformCommand
{
    public void execute(SlaveNode node)
        throws Exception
    {
        node.performAssertValid();
    }
}
