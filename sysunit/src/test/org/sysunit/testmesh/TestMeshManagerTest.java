package org.sysunit.testmesh;

import org.sysunit.testmesh.master.MasterNode;

import org.sysunit.mesh.LocalNodeInfo;
import org.sysunit.mesh.MockNode;
import org.sysunit.model.PhysicalMachineInfo;

public class TestMeshManagerTest
    extends TestMeshTestBase
{
    public void testSlaveHostManagement()
        throws Exception
    {
        MasterNode master = new MasterNode();
        TestMeshManager manager = new TestMeshManager();

        MockNode node1 = new MockNode( "1" );
        MockNode node2 = new MockNode( "2" );
        MockNode node3 = new MockNode( "3" );
        MockNode node4 = new MockNode( "4" );
        MockNode node5 = new MockNode( "5" );

        LocalNodeInfo slave1 = new LocalNodeInfo( master,
                                                  node1 );

        PhysicalMachineInfo physInfo1 = new PhysicalMachineInfo( new String[] { "tag1", "tag2" },
                                                                 new String[] { "jdk4", "jdk3" } );

        LocalNodeInfo slave2 = new LocalNodeInfo( master,
                                                  node2 );

        PhysicalMachineInfo physInfo2 = new PhysicalMachineInfo( new String[] { "tag1", "tag3" },
                                                                 new String[] { "jdk4", "jdk2" } );

        LocalNodeInfo slave3 = new LocalNodeInfo( master,
                                                  node3 );
        
        PhysicalMachineInfo physInfo3 = new PhysicalMachineInfo( new String[] { "tag2", "tag3" },
                                                                 new String[] { "jdk3", "jdk2" } );

        LocalNodeInfo slave4 = new LocalNodeInfo( master,
                                                  node4 );

        PhysicalMachineInfo physInfo4 = new PhysicalMachineInfo( new String[] { "tag3", "tag4" },
                                                                 new String[] { "jdk2", "jdk1" } );

        LocalNodeInfo slave5 = new LocalNodeInfo( master,
                                                  node5 );

        PhysicalMachineInfo physInfo5 = new PhysicalMachineInfo( new String[] { "*" },
                                                                 new String[] { "*" } );

        manager.addSlaveHost( slave1,
                              physInfo1 );

        manager.addSlaveHost( slave2,
                              physInfo2 );

        manager.addSlaveHost( slave3,
                              physInfo3 );

        manager.addSlaveHost( slave4,
                              physInfo4 );

        manager.addSlaveHost( slave5,
                              physInfo5 );

        // --

        assertLength( "5 SlaveHosts",
                      5,
                      manager.getSlaveHosts() );

        // --

        assertLength( "3 SlaveHosts for tag1",
                      3,
                      manager.getSlaveHostsByTag( "tag1" ) );

        assertContains( "slave1 matches tag1",
                        slave1,
                        manager.getSlaveHostsByTag( "tag1" ) );

        assertContains( "slave2 matches tag1",
                        slave2,
                        manager.getSlaveHostsByTag( "tag1" ) );

        assertContains( "slave5 matches tag1",
                        slave5,
                        manager.getSlaveHostsByTag( "tag1" ) );

        // --


        assertLength( "2 SlaveHosts for jdk1",
                      2,
                      manager.getSlaveHostsByJdk( "jdk1" ) );

        assertContains( "slave4 matches jdk1",
                        slave4,
                        manager.getSlaveHostsByJdk( "jdk1" ) );

        assertContains( "slave5 matches jdk1",
                        slave5,
                        manager.getSlaveHostsByJdk( "jdk1" ) );

        // --

        assertLength( "3 SlaveHosts for tag3 jdk2",
                      4,
                      manager.getSlaveHostsByTagAndJdk( "tag3", "jdk2" ) );

        assertContains( "slave2 matches tag3 jdk2",
                        slave2,
                        manager.getSlaveHostsByTagAndJdk( "tag3", "jdk2" ) );

        assertContains( "slave3 matches tag3 jdk2",
                        slave3,
                        manager.getSlaveHostsByTagAndJdk( "tag3", "jdk2" ) );

        assertContains( "slave4 matches tag3 jdk2",
                        slave4,
                        manager.getSlaveHostsByTagAndJdk( "tag3", "jdk2" ) );

        assertContains( "slave5 matches tag3 jdk2",
                        slave5,
                        manager.getSlaveHostsByTagAndJdk( "tag3", "jdk2" ) );

        // -- 

        assertLength( "1 SlaveHost for cheese cheese",
                      1,
                      manager.getSlaveHostsByTagAndJdk( "cheese", "cheese" ) );

        assertContains( "slave5 matches cheese cheese",
                        slave5,
                        manager.getSlaveHostsByTagAndJdk( "cheese", "cheese" ) );

        // --

        assertLength( "5 hosts for (tag)*",
                      5,
                      manager.getSlaveHostsByTag( "*" ) );

        assertLength( "5 hosts for (jdk)*",
                      5,
                      manager.getSlaveHostsByJdk( "*" ) );

        assertLength( "5 hosts for (tag)* (jdk)*",
                      5,
                      manager.getSlaveHostsByTagAndJdk( "*", "*" ) );
    }
}
