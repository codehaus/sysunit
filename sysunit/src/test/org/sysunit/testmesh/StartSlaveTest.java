package org.sysunit.testmesh;

import org.sysunit.mesh.NodeInfo;
import org.sysunit.testmesh.master.MasterNode;
import org.sysunit.testmesh.master.SlaveInfo;
import org.sysunit.testmesh.slavehost.SlaveHostNode;
import org.sysunit.testmesh.slavehost.SlaveHostConfiguration;
import org.sysunit.testmesh.slavehost.StartSlaveCommand;

public class StartSlaveTest
    extends TestMeshTestBase
{
    public void testStartSlaveAndReportBackToMaster()
        throws Exception
    {
        MasterNode    master    = new MasterNode();
        SlaveHostNode slaveHost = new SlaveHostNode( new SlaveHostConfiguration() );

        slaveHost.start();
        master.start();

        Thread.sleep( 3000 );

        assertLength( "master has 1 slave",
                      1,
                      master.getSlaveHosts() );

        NodeInfo slaveHostInfo = master.getSlaveHosts()[0];

        master.executeOn( slaveHostInfo,
                          new StartSlaveCommand( "sun-1.3.1",
                                                 1001 ) );

        Thread.sleep( 3000 );

        assertLength( "1 slave",
                      1,
                      master.getSlaves() );

        assertEquals( "slave is jvmId 1001",
                      1001,
                      master.getSlaves()[0].getJvmId() );

        slaveHost.stop();
        master.stop();
    }
}
