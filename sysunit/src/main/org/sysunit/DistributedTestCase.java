package org.sysunit;

import org.sysunit.model.DistributedSystemTestInfo;
import org.sysunit.model.ScenarioInfo;
import org.sysunit.plan.InfeasibleTestPlanException;
import org.sysunit.testmesh.master.MasterNode;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.AssertionFailedError;

public class DistributedTestCase
    implements Test {

    private ScenarioInfo scenarioInfo;

    public DistributedTestCase(ScenarioInfo scenarioInfo)
    {
        this.scenarioInfo   = scenarioInfo;
    }

    public int countTestCases()
    {
        return 1;
    }

    public ScenarioInfo getScenarioInfo()
    {
        return this.scenarioInfo;
    }

    public String getName()
    {
        return getScenarioInfo().getName() + "(" + getScenarioInfo().getSystemTestInfo().getName() + ")";
    }

    public void run(TestResult testResult)
    {
        testResult.startTest( this );

        try
        {
            MasterNode master = new MasterNode( getScenarioInfo() );

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
        catch (InfeasibleTestPlanException e)
        {
            testResult.addFailure( this,
                                   new AssertionFailedError( e.getMessage() ) );
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

    public String toString()
    {
        return getName();
    }
}
