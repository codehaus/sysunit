package org.sysunit;

import org.sysunit.builder.DistributedSystemTestInfoBuilder;
import org.sysunit.model.DistributedSystemTestInfo;
import org.sysunit.model.ScenarioInfo;
import org.sysunit.testmesh.slavehost.SlaveHostNode;
import org.sysunit.testmesh.slavehost.SlaveHostConfiguration;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.textui.TestRunner;

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Enumeration;

public class DistributedTestCaseTest
    extends SysUnitTestBase
{
    private List slaveHosts;

    private TestRunner testRunner;
    private TestResult testResult;

    public void setUp()
        throws Exception
    {
        super.setUp();
        this.testRunner = new TestRunner();
        this.slaveHosts = new ArrayList();
    }

    public void tearDown()
        throws Exception
    {
        for ( Iterator hostIter = this.slaveHosts.iterator() ;
              hostIter.hasNext() ; )
        {
            SlaveHostNode host = (SlaveHostNode) hostIter.next();

            host.stop();
        }

        this.testRunner = null;
        this.testResult = null;

        super.tearDown();
    }

    public void testNoOpNoSyncSuccessful()
        throws Exception
    {
        System.err.println( "foo" );
        startSlaveHosts( 2 );
        System.err.println( "bar" );

        runTest( "no-op" );
        System.err.println( "baz" );

        assertErrors( 0 );
        assertFailures( 0 );
    }

    /*
    public void testNoOpWithSyncSuccessful()
        throws Exception
    {
        startSlaveHosts( 2 );

        runTest( "no-op-sync" );

        assertErrors( 0 );
        assertFailures( 0 );
    }

    public void testSimpleSyncSuccessful()
        throws Exception
    {
        startSlaveHosts( 2 );

        runTest( "simple-sync" );

        assertErrors( 0 );
        assertFailures( 0 );
    }

    public void testThrowInRun()
        throws Exception
    {
        startSlaveHosts( 3 );

        runTest( "throwing" );

        assertErrors( 1 );
        assertFailures( 0 );

        dump();
    }
    */

    void dump()
    {
        Enumeration errors = this.testResult.errors();

        while ( errors.hasMoreElements() )
        {
            System.err.println( "error: " + errors.nextElement() );
        }

        Enumeration failures = this.testResult.failures();

        while ( failures.hasMoreElements() )
        {
            System.err.println( "failure: " + failures.nextElement() );
        }
    }

    void runTest(String name)
        throws Exception
    {
        InputStream in = getClass().getResourceAsStream( "tests/" + name + ".systest" );

        if ( in == null )
        {
            fail( "no such test <" + name + ".systest>" );
        }

        DistributedSystemTestInfo testInfo = DistributedSystemTestInfoBuilder.build( in );

        ScenarioInfo scenarioInfo = new ScenarioInfo( "none" );

        DistributedTestCase test = new DistributedTestCase( testInfo,
                                                            scenarioInfo );

        runTest( test );
    }

    void runTest(DistributedTestCase test)
        throws Exception
    {
        this.testResult = this.testRunner.doRun( test );
    }

    void startSlaveHosts(int num)
        throws Exception
    {
        for ( int i = 0 ; i < num ; ++i )
        {
            startSlaveHost();
        }
    }

    SlaveHostNode startSlaveHost()
        throws Exception
    {
        SlaveHostConfiguration config = new SlaveHostConfiguration();
        config.addTag( "*" );

        SlaveHostNode slaveHost = new SlaveHostNode( config );

        slaveHost.start();

        this.slaveHosts.add( slaveHost );

        return slaveHost;
    }

    void assertErrors(int num)
    {
        if ( this.testResult.errorCount() != num )
        {
            fail( "expected <" + num + "> errors, but found <" + this.testResult.errorCount() + ">" );
        }
    }

    void assertFailures(int num)
    {
        if ( this.testResult.failureCount() != num )
        {
            fail( "expected <" + num + "> errors, but found <" + this.testResult.failureCount() + ">" );
        }
    }
}
