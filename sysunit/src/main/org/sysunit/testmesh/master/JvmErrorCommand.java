package org.sysunit.testmesh.master;

public class JvmErrorCommand
    extends MasterCommand
{
    public JvmErrorCommand()
    {
    }

    public void execute(MasterNode node)
        throws Exception
    {
        node.jvmError();
    }
      
}
