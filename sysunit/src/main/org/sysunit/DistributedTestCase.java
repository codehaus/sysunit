package org.sysunit;

import org.sysunit.model.DistributedSystemTestInfo;
import org.sysunit.model.ScenarioInfo;
import org.sysunit.testmesh.master.MasterNode;

import junit.framework.Test;
import junit.framework.TestResult;

public class DistributedTestCase
    implements Test {

    private DistributedSystemTestInfo systemTestInfo;
    private ScenarioInfo scenarioInfo;

    public DistributedTestCase(DistributedSystemTestInfo systemTestInfo,
                               ScenarioInfo scenarioInfo)
    {
        this.systemTestInfo = systemTestInfo;
        this.scenarioInfo   = scenarioInfo;
    }

    public int countTestCases()
    {
        return 1;
    }

    public DistributedSystemTestInfo getSystemTestInfo()
    {
        return this.systemTestInfo;
    }

    public ScenarioInfo getScenarioInfo()
    {
        return this.scenarioInfo;
    }

    public String getName()
    {
        return getSystemTestInfo().getName();
    }

    public void run(TestResult testResult)
    {
        testResult.startTest( this );

        try
        {
            MasterNode master = new MasterNode( getSystemTestInfo(),
                                                getScenarioInfo() );

            master.start();

            try
            {
                Throwable[] throwables = master.runTest();
                
                for ( int i = 0 ; i < throwables.length ; ++i )
                {
                    testResult.addError( this,
                                         throwables[ i ] );
                }
            }
            finally
            {
                master.stop();
            }
        }
        catch (Throwable t)
        {
            testResult.addError( this,
                                 t );
        }
        finally
        {
            testResult.endTest( this );
        }
    }
}
