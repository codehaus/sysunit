package org.sysunit.testmesh.master;

public class NotifyFullyBlockedCommand
    extends MasterCommand
{
    private int jvmId;

    public NotifyFullyBlockedCommand(int jvmId)
    {
        this.jvmId = jvmId;
    }

    public int getJvmId()
    {
        return this.jvmId;
    }

    public void execute(MasterNode node)
    {
        node.notifyFullyBlocked( this.jvmId );
    }
}
