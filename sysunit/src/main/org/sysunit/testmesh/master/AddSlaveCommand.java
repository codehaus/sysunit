package org.sysunit.testmesh.master;

public class AddSlaveCommand
    extends MasterCommand
{
    private int jvmId;

    public AddSlaveCommand(int jvmId)
    {
        this.jvmId = jvmId;
    }

    public int getJvmId()
    {
        return this.jvmId;
    }

    public void execute(MasterNode node)
        throws Exception
    {
        node.addSlave( getOrigin(),
                       getJvmId() );
    }
}
