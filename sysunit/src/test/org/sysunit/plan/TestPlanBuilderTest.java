package org.sysunit.plan;

import org.sysunit.model.DistributedSystemTestInfo;
import org.sysunit.model.JvmInfo;
import org.sysunit.model.ScenarioInfo;
import org.sysunit.model.PhysicalMachineInfo;

import org.sysunit.testmesh.TestMeshManager;
import org.sysunit.testmesh.master.MasterNode;
import org.sysunit.testmesh.slavehost.SlaveHostNode;
import org.sysunit.testmesh.slavehost.SlaveHostConfiguration;

public class TestPlanBuilderTest
    extends PlanTestBase
{
    private DistributedSystemTestInfo systemTest;
    private ScenarioInfo scenario;
    private MasterNode master;

    public void setUp()
    {
        this.systemTest = new DistributedSystemTestInfo( "system.test" );
        this.scenario   = new ScenarioInfo( "scenario" );
        this.master     = new MasterNode();
    }

    public void tearDown()
    {
        this.systemTest = null;
        this.scenario   = null;
        this.master     = null;
    }

    public void testNoTagOrJdkSpecified_feasible()
        throws Exception
    {
        slaveHost( "slave1",
                   new String[] { "win32" },
                   new String[] { "sun1.3.1" } );

        jvm( "jvm1",
             1,
             null,
             null );

        TestPlan plan = testPlan();

        assertSame( "DistributedSystemTestInfo is recorded",
                    this.systemTest,
                    plan.getSystemTest() );

        assertLength( "1 binding",
                      1,
                      plan.getJvmBindings() );

        JvmBinding binding = plan.getJvmBindings()[0];

        assertEquals( "binding 1 is for jvm1",
                      "jvm1",
                      binding.getJvmInfo().getName() );

        assertEquals( "binding 1 is for slave1",
                      "slave1",
                      binding.getNodeInfo().getName() );
    }

    public void testTagSpecified_infeasible()
        throws Exception
    {
        slaveHost( "slave1",
                   new String[] { "win32" },
                   new String[] { "sun1.3.1" } );

        jvm( "jvm1",
             1,
             "linux",
             null );

        try
        {
            testPlan();
            fail( "should have thrown InfeasibleTestPlanException" );
        }
        catch (InfeasibleTestPlanException e)
        {
            // expected and correct
        }
    }

    public void testJdkSpecified_infeasible()
        throws Exception
    {
        slaveHost( "slave1",
                   new String[] { "win32" },
                   new String[] { "sun1.3.1" } );

        jvm( "jvm1",
             1,
             null,
             "sun1.4.1" );

        try
        {
            testPlan();
            fail( "should have thrown InfeasibleTestPlanException" );
        }
        catch (InfeasibleTestPlanException e)
        {
            // expected and correct
        }
    }

    public void testTagAndJdkSpecified_infeasible()
        throws Exception
    {
        slaveHost( "slave1",
                   new String[] { "win32" },
                   new String[] { "sun1.3.1" } );

        jvm( "jvm1",
             1,
             "linux",
             "sun1.4.1" );

        try
        {
            testPlan();
            fail( "should have thrown InfeasibleTestPlanException" );
        }
        catch (InfeasibleTestPlanException e)
        {
            // expected and correct
        }
    }

    public void testTagAndJdkSpecified_matchTagOnly_infeasible()
        throws Exception
    {
        slaveHost( "slave1",
                   new String[] { "win32" },
                   new String[] { "sun1.3.1" } );

        jvm( "jvm1",
             1,
             "win32",
             "sun1.4.1" );

        try
        {
            testPlan();
            fail( "should have thrown InfeasibleTestPlanException" );
        }
        catch (InfeasibleTestPlanException e)
        {
            // expected and correct
        }
    }

    public void testTagAndJdkSpecified_matchJdkOnly_infeasible()
        throws Exception
    {
        slaveHost( "slave1",
                   new String[] { "win32" },
                   new String[] { "sun1.3.1" } );

        jvm( "jvm1",
             1,
             "linux",
             "sun1.3.1" );

        try
        {
            testPlan();
            fail( "should have thrown InfeasibleTestPlanException" );
        }
        catch (InfeasibleTestPlanException e)
        {
            // expected and correct
        }
    }

    public void testRoundRobin_singleMatches()
        throws Exception
    {
        slaveHost( "slave1",
                   new String[] { "win32" },
                   new String[] { "sun1.3.1" } );

        slaveHost( "slave2",
                   new String[] { "linux" },
                   new String[] { "sun1.3.1" } );


        jvm( "jvm1",
             3,
             "win32",
             "sun1.3.1" );

        jvm( "jvm2",
             2,
             "linux",
             "sun1.3.1" );


        TestPlan plan = testPlan();

        assertLength( "3 bindings",
                      5,
                      plan.getJvmBindings() );

        assertBindings( "jvm1", "slave1", 3,
                        plan.getJvmBindings() );

        assertBindings( "jvm2", "slave2", 2,
                        plan.getJvmBindings() );
    }

    public void testRoundRobin_multipleMachines()
        throws Exception
    {
        slaveHost( "slave1",
                   new String[] { "win32" },
                   new String[] { "sun1.3.1" } );

        slaveHost( "slave2",
                   new String[] { "win32" },
                   new String[] { "sun1.3.1" } );

        slaveHost( "slave3",
                   new String[] { "linux" },
                   new String[] { "sun1.3.1" } );

        slaveHost( "slave4",
                   new String[] { "linux" },
                   new String[] { "sun1.3.1" } );


        jvm( "jvm1",
             4,
             "win32",
             "sun1.3.1" );

        jvm( "jvm2",
             2,
             "linux",
             "sun1.3.1" );


        TestPlan plan = testPlan();

        assertLength( "3 bindings",
                      6,
                      plan.getJvmBindings() );

        assertBindings( "jvm1", "slave1", 2,
                        plan.getJvmBindings() );

        assertBindings( "jvm1", "slave2", 2,
                        plan.getJvmBindings() );

        assertBindings( "jvm2", "slave3", 1,
                        plan.getJvmBindings() );

        assertBindings( "jvm2", "slave4", 1,
                        plan.getJvmBindings() );
    }

    TestPlan testPlan()
        throws InfeasibleTestPlanException
    {
        TestPlanBuilder builder = new TestPlanBuilder( this.systemTest,
                                                       this.scenario,
                                                       this.master.getTestMeshManager() );

        return builder.buildTestPlan();
    }

    void slaveHost(String name,
                   String[] tags,
                   String[] jdks)
    {
        SlaveHostConfiguration config = new SlaveHostConfiguration();

        for ( int i = 0 ; i < tags.length ; ++i )
        {
            config.addTag( tags[i] );
        }

        for ( int i = 0 ; i < tags.length ; ++i )
        {
            config.addJdk( jdks[i],
                           null );
        }

        SlaveHostNode slaveHost = new SlaveHostNode( name,
                                                     config );

        this.master.addSlaveHost( slaveHost.getLocalNodeInfo(),
                                  slaveHost.getPhysicalMachineInfo() );
    }

    void jvm(String name,
             int count,
             String tag,
             String jdk)
    {
        JvmInfo jvm = new JvmInfo( name,
                                   count );

        this.systemTest.addJvm( jvm );

        if ( tag != null )
        {
            this.scenario.setTag( jvm,
                                  tag );
        }

        if ( jdk != null )
        {
            this.scenario.setJdk( jvm,
                                  jdk );
        }
    }

    void assertBindings(String jvm,
                        String slave,
                        int num,
                        JvmBinding bindings[])
    {
        int count = 0;

        for ( int i = 0 ; i < bindings.length ; ++i )
        {
            if ( bindings[i].getJvmInfo().getName().equals( jvm )
                 &&
                 bindings[i].getNodeInfo().getName().equals( slave ) )
            {
                ++count;
            }
        }

        if ( count != num )
        {
            fail( "expected <" + num + "> bindings of " + jvm + "/" + slave + " but found <" + count + ">" );
        }
    }
}
