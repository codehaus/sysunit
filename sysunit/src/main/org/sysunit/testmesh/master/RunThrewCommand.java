package org.sysunit.testmesh.master;

public class RunThrewCommand
    extends TestThrewCommand
{
    public RunThrewCommand(int jvmId,
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
        node.runThrew( getJvmId(),
                       getTBeanId(),
                       getThrown() );
                       
    }
}
