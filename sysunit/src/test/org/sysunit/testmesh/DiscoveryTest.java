package org.sysunit.testmesh;

import org.sysunit.testmesh.master.MasterNode;
import org.sysunit.testmesh.slavehost.SlaveHostNode;
import org.sysunit.testmesh.slavehost.SlaveHostConfiguration;

public class DiscoveryTest
    extends TestMeshTestBase
{
    public void testDiscovery_OverMCast()
        throws Exception
    {
        SlaveHostNode slaveHost1 = new SlaveHostNode( new SlaveHostConfiguration() );
        SlaveHostNode slaveHost2 = new SlaveHostNode( new SlaveHostConfiguration() );
        SlaveHostNode slaveHost3 = new SlaveHostNode( new SlaveHostConfiguration() );

        MasterNode master = new MasterNode();
        
        slaveHost1.start();
        slaveHost2.start();
        slaveHost3.start();

        master.start();

        
        master.stop();

        slaveHost1.stop();
        slaveHost2.stop();
        slaveHost3.stop();
        
        if ( master.isMulticastEnabled() )
        {
            assertLength( "3 slaves discovered",
                          3,
                          master.getSlaveHosts() );
            
            assertUnique( "all slaves are different",
                          master.getSlaveHosts() );
        }
        else
        {
            assertLength( "1 slave discovered",
                          1,
                          master.getSlaveHosts() );
        }
    }

    public void testDiscovery_OverBCast()
        throws Exception
    {
        SlaveHostNode slaveHost1 = new SlaveHostNode( new SlaveHostConfiguration() );

        MasterNode master = new MasterNode();

        slaveHost1.disableMulticast();
        master.disableMulticast();

        slaveHost1.start();
        master.start();

        
        master.stop();
        slaveHost1.stop();
        
        assertLength( "1 slaves discovered",
                      1,
                      master.getSlaveHosts() );
    }
}
