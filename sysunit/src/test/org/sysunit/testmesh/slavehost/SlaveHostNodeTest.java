package org.sysunit.testmesh.slavehost;

public class SlaveHostNodeTest
    extends SlaveHostTestBase
{
    public void testStartStop()
        throws Exception
    {
        SlaveHostNode node = new SlaveHostNode( new SlaveHostConfiguration() );

        node.start();
        node.stop();
    }
}
