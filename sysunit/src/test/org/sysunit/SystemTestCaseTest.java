package org.sysunit;

import org.sysunit.tests.*;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.textui.TestRunner;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class SystemTestCaseTest
    extends SysUnitTestBase
{
    private TestRunner testRunner;
    private TestResult testResult;
    private WatchedSystemTestCase test;

    public void setUp()
        throws Exception
    {
        super.setUp();
        this.testRunner = new TestRunner();
        //this.testRunner = new TestRunner( new PrintStream( new ByteArrayOutputStream() ) );
    }

    public void tearDown()
        throws Exception
    {
        this.testRunner = null;
        this.testResult = null;
        super.tearDown();
    }

    public void testThreadOnlySuccessful()
        throws Exception
    {
        runTest( ThreadOnlySuccessfulTest.class );

        assertErrors( 0 );
        assertFailures( 0 );

        assertTouches( 5 );
        assertTouch( "setUp()" );
        assertTouch( "tearDown()" );
        assertTouch( "assertValid()" );
        assertTouch( "threadOne()" );
        assertTouch( "threadTwo()" );
    }

    public void testThreadOnlyWithSyncSuccessful()
        throws Exception
    {
        runTest( ThreadOnlyWithSyncSuccessfulTest.class );

        assertErrors( 0 );
        assertFailures( 0 );

        assertTouches( 7 );
        assertTouch( "setUp()" );
        assertTouch( "tearDown()" );
        assertTouch( "assertValid()" );
        assertTouch( "threadOne() before sync" );
        assertTouch( "threadTwo() before sync" );
        assertTouch( "threadOne() after sync" );
        assertTouch( "threadTwo() after sync" );
    }

    public void testThreadOnlyWithSync_Errors()
        throws Exception
    {
        runTest( ThreadOnlyWithSync_Errors.class );

        assertErrors( 1 );
        assertFailures( 0 );

        //assertTouches( 4 );
        assertTouch( "threadOne() a" );
        assertTouch( "threadTwo() a" );
        //assertTouch( "threadOne() b" );
        //assertTouch( "threadTwo() b" );
    }

    public void testThreadOnlyWithSync_Failures()
        throws Exception
    {
        runTest( ThreadOnlyWithSync_Failures.class );

        assertErrors( 0 );
        assertFailures( 1 );

        assertTouches( 4 );
        assertTouch( "threadOne() a" );
        assertTouch( "threadTwo() a" );
        assertTouch( "threadOne() b" );
        assertTouch( "threadTwo() b" );
    }


    public void testThreadOnlyWithSync_Cascading()
        throws Exception
    {
        runTest( ThreadOnlyWithSync_Cascading.class );

        assertErrors( 0 );
        assertFailures( 0 );

        assertTouches( 9 );

        assertTouch( "threadOne() a" );
        assertTouch( "threadTwo() a" );
        assertTouch( "threadThree() a" );

        assertTouch( "threadTwo() b" );
        assertTouch( "threadOne() b" );
        assertTouch( "threadThree() b" );

        assertTouch( "threadTwo() c" );
        assertTouch( "threadThree() c" );

        assertTouch( "threadThree() d" );
    }

    public void testThreadOnlyWithTimeout()
        throws Exception
    {
        long start = System.currentTimeMillis();
        runTest( ThreadOnlyWithTimeout.class );
        long stop = System.currentTimeMillis();

        assertTrue( "ran for less than 3s",
                    (stop-start) < 3000 );

        assertErrors( 2 );
        assertFailures( 1 );

        assertTouches( 2 );
        assertTouch( "threadOne() a" );
        assertTouch( "threadTwo() a" );
    }

    public void testThreadsAndTBeansSuccessful()
        throws Exception
    {
        runTest( ThreadsAndTBeansSuccessful.class );

        assertErrors( 0 );
        assertFailures( 0 );

        assertTouches( 13 );
        assertTouch( "setUp()" );
        assertTouch( "tearDown()" );
        assertTouch( "assertValid()" );
        assertTouch( "threadOne()" );
        assertTouch( "threadTwo()" );
        assertTouch( "tbeanOne setUp()" );
        assertTouch( "tbeanOne tearDown()" );
        assertTouch( "tbeanOne assertValid()" );
        assertTouch( "tbeanOne run()" );
        assertTouch( "tbeanTwo setUp()" );
        assertTouch( "tbeanTwo tearDown()" );
        assertTouch( "tbeanTwo assertValid()" );
        assertTouch( "tbeanTwo run()" );
    }

    public void testThreadsAndTBeansWithSyncSuccessful()
        throws Exception
    {
        runTest( ThreadsAndTBeansWithSyncSuccessful.class );

        assertErrors( 0 );
        assertFailures( 0 );

        assertTouches( 25 );

        assertTouch( "setUp()" );
        assertTouch( "tearDown()" );
        assertTouch( "assertValid()" );

        assertTouch( "threadOne() start" );
        assertTouch( "threadOne() end" );

        assertTouch( "threadTwo() start" );
        assertTouch( "threadTwo() end" );

        assertTouch( "tbeanOne setUp()" );
        assertTouch( "tbeanOne tearDown()" );
        assertTouch( "tbeanOne assertValid()" );

        assertTouch( "tbeanOne start run()" );
        assertTouch( "tbeanOne before sync(a)" );
        assertTouch( "tbeanOne after sync(a)" );
        assertTouch( "tbeanOne before sync(b)" );
        assertTouch( "tbeanOne after sync(b)" );
        assertTouch( "tbeanOne end run()" );

        assertTouch( "tbeanTwo setUp()" );
        assertTouch( "tbeanTwo tearDown()" );
        assertTouch( "tbeanTwo assertValid()" );

        assertTouch( "tbeanTwo start run()" );
        assertTouch( "tbeanTwo before sync(a)" );
        assertTouch( "tbeanTwo after sync(a)" );
        assertTouch( "tbeanTwo before sync(b)" );
        assertTouch( "tbeanTwo after sync(b)" );
        assertTouch( "tbeanTwo end run()" );
    }

    public void testThreadsOnly_ThrowInSetUp()
        throws Exception
    {
        runTest( ThreadsOnly_ThrowInSetUp.class );

        assertErrors( 1 );
        assertFailures( 0 );

        assertTouches( 1 );

        assertTouch( "setUp()" );
    }

    public void testThreadsOnly_FailInAssertValid()
        throws Exception
    {
        runTest( ThreadsOnly_FailInAssertValid.class );

        assertErrors( 0 );
        assertFailures( 1 );

        assertTouches( 5 );
        assertTouch( "setUp()" );
        assertTouch( "threadOne()" );
        assertTouch( "threadTwo()" );
        assertTouch( "assertValid()" );
        assertTouch( "tearDown()" );
    }

    public void testThreadsOnly_ThrowInTearDown()
        throws Exception
    {
        runTest( ThreadsOnly_ThrowInTearDown.class );

        assertErrors( 1 );
        assertFailures( 0 );

        assertTouches( 5 );
        assertTouch( "setUp()" );
        assertTouch( "threadOne()" );
        assertTouch( "threadTwo()" );
        assertTouch( "assertValid()" );
        assertTouch( "tearDown()" );
    }

    public void testThreadsAndTBeans_ThrowInSetUp()
        throws Exception
    {
        runTest( ThreadsAndTBeans_ThrowInSetUp.class );

        assertErrors( 2 );
        assertFailures( 0 );

        assertTouches( 6 );

        assertTouch( "setUp()" );
        assertTouch( "tbeanOne setUp()" );
        assertTouch( "tbeanTwo setUp()" );
        assertTouch( "tbeanOne tearDown()" );
        assertTouch( "tbeanTwo tearDown()" );
        assertTouch( "tearDown()" );
    }

    void runTest(Class testClass)
        throws Exception
    {
        WatchedSystemTestCase test = (WatchedSystemTestCase) testClass.newInstance();

        runTest( test );
    }

    void runTest(WatchedSystemTestCase test)
        throws Exception
    {
        this.test       = test;
        this.testResult = this.testRunner.doRun( test );
    }

    void assertTouch(String id)
    {
        assertContains( "touched by " + id,
                        id,
                        this.test.getTouches() );
    }

    void assertTouches(int num)
    {
        assertLength( num + " touches expected, found " + Arrays.asList( this.test.getTouches() ),
                      num,
                      this.test.getTouches() );
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
