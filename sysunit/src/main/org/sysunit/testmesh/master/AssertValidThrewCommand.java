package org.sysunit.testmesh.master;

public class AssertValidThrewCommand
    extends TestThrewCommand
{
    public AssertValidThrewCommand(int jvmId,
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
        node.assertValidThrew( getJvmId(),
                               getTBeanId(),
                               getThrown() );
    }
}
