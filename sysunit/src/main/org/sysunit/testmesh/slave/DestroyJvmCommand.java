package org.sysunit.testmesh.slave;

public class DestroyJvmCommand
    extends SlaveCommand
{
    public DestroyJvmCommand()
    {
    }

    public void execute(SlaveNode node)
        throws Exception
    {
        node.destroyJvm();
    }
}
