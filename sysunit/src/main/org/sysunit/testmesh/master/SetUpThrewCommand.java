package org.sysunit.testmesh.master;

public class SetUpThrewCommand
    extends TestThrewCommand
{
    public SetUpThrewCommand(int jvmId,
                             String tbeanId,
                             Throwable thrown)
    {
        super( jvmId,
               tbeanId,
               thrown );
    }

    public void execute(MasterNode node)
        throws Exception
    {
        node.setUpThrew( getJvmId(),
                         getTBeanId(),
                         getThrown() );
                          
    }
}
