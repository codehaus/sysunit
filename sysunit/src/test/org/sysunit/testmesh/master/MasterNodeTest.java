package org.sysunit.testmesh.master;

import org.sysunit.mesh.LocalNodeInfo;
import org.sysunit.mesh.MockNode;
import org.sysunit.model.PhysicalMachineInfo;

public class MasterNodeTest
    extends MasterTestBase
{
    public void testStartStop()
        throws Exception
    {
        MasterNode node = new MasterNode( null,
                                          null );

        node.start();
        node.stop();
    }
}
