package org.sysunit.testmesh;

import org.sysunit.testmesh.master.MasterNode;
import org.sysunit.testmesh.slavehost.SlaveHostNode;
import org.sysunit.testmesh.slavehost.SlaveHostConfiguration;

public class BroadcastDiscoveryTest
    extends TestMeshTestBase
{
    public void testDiscovery()
        throws Exception
    {
        SlaveHostNode slaveHost1 = new SlaveHostNode( new SlaveHostConfiguration() );

        MasterNode master = new MasterNode();

        slaveHost1.disableMulticast();
        master.disableMulticast();

        slaveHost1.start();
        master.start();

        Thread.sleep( 5000 );

        
        master.stop();
        slaveHost1.stop();
        
        assertLength( "1 slaves discovered",
                      1,
                      master.getSlaveHosts() );
    }
}
