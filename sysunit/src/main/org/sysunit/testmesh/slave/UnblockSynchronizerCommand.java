package org.sysunit.testmesh.slave;

public class UnblockSynchronizerCommand
    extends SlaveCommand
{
    private int unblockSequence;

    public UnblockSynchronizerCommand(int unblockSequence)
    {
        this.unblockSequence = unblockSequence;
    }

    public void execute(SlaveNode node)
        throws Exception
    {
        node.unblockSynchronizer( this.unblockSequence );
    }
}
