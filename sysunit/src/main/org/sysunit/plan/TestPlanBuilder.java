package org.sysunit.plan;

import org.sysunit.model.DistributedSystemTestInfo;
import org.sysunit.model.ScenarioInfo;
import org.sysunit.model.JvmInfo;
import org.sysunit.mesh.NodeInfo;
import org.sysunit.testmesh.TestMeshManager;

import java.util.List;
import java.util.ArrayList;

public class TestPlanBuilder
{
    private static final JvmInfo[] EMPTY_JVMINFO_ARRAY = new JvmInfo[0];

    private DistributedSystemTestInfo systemTestInfo;
    private ScenarioInfo scenarioInfo;
    private TestMeshManager testMeshManager;

    public TestPlanBuilder(DistributedSystemTestInfo systemTestInfo,
                           ScenarioInfo scenarioInfo,
                           TestMeshManager testMeshManager)
    {
        this.systemTestInfo  = systemTestInfo;
        this.scenarioInfo    = scenarioInfo;
        this.testMeshManager = testMeshManager;
    }

    public DistributedSystemTestInfo getSystemTest()
    {
        return this.systemTestInfo;
    }

    public ScenarioInfo getScenario()
    {
        return this.scenarioInfo;
    }

    public TestMeshManager getTestMeshManager()
    {
        return this.testMeshManager;
    }

    public TestPlan buildTestPlan()
        throws InfeasibleTestPlanException
    {
        JvmInfo[]    jvms       = getSystemTest().getJvms();
        NodeInfo[][] candidates = new NodeInfo[ jvms.length ][];

        for ( int i = 0 ; i < jvms.length ; ++i )
        {
            String tag = getScenario().getTag( jvms[ i ] );
            String jdk = getScenario().getJdk( jvms[ i ] );

            NodeInfo[] matching = null;

            if ( tag == null
                 &&
                 jdk == null )
            {
                matching = getTestMeshManager().getSlaveHosts();
            }
            else if ( tag == null )
            {
                matching = getTestMeshManager().getSlaveHostsByJdk( jdk );
            }
            else if ( jdk == null )
            {
                matching = getTestMeshManager().getSlaveHostsByTag( tag );
            }
            else
            {
                matching = getTestMeshManager().getSlaveHostsByTagAndJdk( tag,
                                                                          jdk );
            }

            candidates[ i ] = matching;
        }

        List unsatisfiedJvms = new ArrayList();

        for ( int i = 0 ; i < candidates.length ; ++i )
        {
            if ( candidates[ i ].length == 0 )
            {
                unsatisfiedJvms.add( jvms[ i ] );
            }
        }

        if ( ! unsatisfiedJvms.isEmpty() )
        {
            throw new InfeasibleTestPlanException( getSystemTest(),
                                                   (JvmInfo[]) unsatisfiedJvms.toArray( EMPTY_JVMINFO_ARRAY ) );
        }

        TestPlan testPlan = new TestPlan( getSystemTest() );

        int jvmId = 1000;

        for ( int i = 0 ; i < jvms.length ; ++i )
        {
            int c = 0;

            for ( int j = 0 ; j < jvms[ i ].getCount() ; ++j )
            {
                JvmBinding binding = new JvmBinding( ++jvmId,
                                                     jvms[ i ],
                                                     candidates[ i ][ c ] );

                if ( ++c == candidates[ i ].length )
                {
                    c = 0;
                }

                testPlan.addJvmBinding( binding );
            }
        }

        return testPlan;
    }
}
