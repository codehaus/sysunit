package org.sysunit.testmesh.slavehost;

public class CollectOutputsCommand
    extends SlaveHostCommand
{
    public void execute(SlaveHostNode node)
        throws Exception
    {
        node.collectOutputs( getOrigin() );
    }
}
