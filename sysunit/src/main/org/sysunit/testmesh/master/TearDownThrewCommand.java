package org.sysunit.testmesh.master;

public class TearDownThrewCommand
    extends TestThrewCommand
{
    public TearDownThrewCommand(int jvmId,
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
        node.tearDownThrew( getJvmId(),
                            getTBeanId(),
                            getThrown() );
    }
}
