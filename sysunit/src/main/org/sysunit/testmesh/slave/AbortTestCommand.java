package org.sysunit.testmesh.slave;

public class AbortTestCommand
    extends SlaveCommand
{
    public AbortTestCommand()
    {

    }

    public void execute(SlaveNode node)
        throws Exception
    {
        node.abortTest();
    }
}
