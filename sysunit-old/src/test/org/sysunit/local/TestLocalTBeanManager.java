package org.sysunit.local;

import org.sysunit.SystemTestCase;
import org.sysunit.SysUnitTestCase;
import org.sysunit.SingleTBeanCase;
import org.sysunit.DoubleTBeanCase;
import org.sysunit.MockTBean;
import org.sysunit.SleepTBean;
import org.sysunit.FailTBean;
import org.sysunit.ErrorTBean;
import org.sysunit.ValidationFailTBean;
import org.sysunit.ValidationErrorTBean;
import org.sysunit.MockSynchronizableTBean;
import org.sysunit.WatchdogException;

import junit.framework.TestResult;
import junit.framework.TestFailure;

import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;

public class TestLocalTBeanManager
    extends SysUnitTestCase {

    public void testConstruct()
        throws Exception {
        LocalTBeanManager manager = new LocalTBeanManager();

        manager.initialize();

        assertEmpty( manager.getTBeanThreads() );
        assertEmpty( manager.getTBeans() );
    }

    public void testStartTBeans_NotSynchronizable()
        throws Throwable {

        MockTBean tbean = new MockTBean();

        SingleTBeanCase testCase = new SingleTBeanCase( tbean );

        testCase.initializeFactories();

        LocalTBeanManager manager = new LocalTBeanManager();

        TestResult testResult = new TestResult();

        manager.startTBeans( testCase,
                             testResult );

        Thread.sleep( 2000 );

        assertTrue( tbean.hasRun() );

        assertEmpty( manager.getSynchronizer().getRegisteredTBeans() );
    }

    public void testStartTBeans_Synchronizable()
        throws Throwable {

        SleepTBean tbean = new SleepTBean( 3000 );

        SingleTBeanCase testCase = new SingleTBeanCase( tbean );

        testCase.initializeFactories();

        LocalTBeanManager manager = new LocalTBeanManager();

        TestResult testResult = new TestResult();

        manager.startTBeans( testCase,
                             testResult );

        assertLength( 1,
                      manager.getSynchronizer().getRegisteredTBeans() );

        assertContainsEquals( "One",
                              manager.getSynchronizer().getRegisteredTBeans() );

        Thread.sleep( 5000 );

        assertTrue( tbean.hasRun() );

        //assertLength( 0,
                      //manager.getSynchronizer().getRegisteredTBeans() );
    }

    public void testWaitForTBeans_Single_NoTimeout()
        throws Throwable {

        SleepTBean tbean = new SleepTBean( 5000 );

        SingleTBeanCase testCase = new SingleTBeanCase( tbean );

        testCase.initializeFactories();

        LocalTBeanManager manager = new LocalTBeanManager();

        TestResult testResult = new TestResult();

        long start = new Date().getTime();

        manager.startTBeans( testCase,
                             testResult );

        manager.waitForTBeans( testCase,
                               0 );

        long stop = new Date().getTime();

        long runTime = stop - start;

        assertTrue( runTime >= 5000 );
        assertTrue( runTime < 8000 );

        assertTrue( tbean.hasRun() );
    }

    public void testWaitForTBeans_Double_NoTimeout()
        throws Throwable {

        SleepTBean tbeanOne = new SleepTBean( 5000 );
        SleepTBean tbeanTwo = new SleepTBean( 5000 );

        DoubleTBeanCase testCase = new DoubleTBeanCase( tbeanOne,
                                                        tbeanTwo );

        testCase.initializeFactories();

        LocalTBeanManager manager = new LocalTBeanManager();

        TestResult testResult = new TestResult();

        long start = new Date().getTime();

        manager.startTBeans( testCase,
                             testResult );

        manager.waitForTBeans( testCase,
                               0 );

        long stop = new Date().getTime();

        long runTime = stop - start;

        assertTrue( runTime >= 5000 );
        assertTrue( runTime < 6000 );

        assertTrue( tbeanOne.hasRun() );
        assertTrue( tbeanTwo.hasRun() );
    }

    public void testWaitForTBeans_Double_WithTimeout_NoExpiration()
        throws Throwable {

        SleepTBean tbeanOne = new SleepTBean( 5000 );
        SleepTBean tbeanTwo = new SleepTBean( 1000 );

        DoubleTBeanCase testCase = new DoubleTBeanCase( tbeanOne,
                                                        tbeanTwo );

        testCase.initializeFactories();

        LocalTBeanManager manager = new LocalTBeanManager();

        TestResult testResult = new TestResult();

        long start = new Date().getTime();

        manager.startTBeans( testCase,
                             testResult );

        manager.waitForTBeans( testCase,
                               6000 );

        long stop = new Date().getTime();

        long runTime = stop - start;

        assertTrue( runTime >= 5000 );
        assertTrue( runTime < 6000 );

        assertTrue( tbeanOne.hasRun() );
        assertTrue( tbeanTwo.hasRun() );
    }

    public void testWaitForTBeans_Single_WithTimeout_Expiration()
        throws Throwable {

        SleepTBean tbeanOne = new SleepTBean( 5000 );

        SingleTBeanCase testCase = new SingleTBeanCase( tbeanOne );

        testCase.initializeFactories();

        LocalTBeanManager manager = new LocalTBeanManager();

        TestResult testResult = new TestResult();

        manager.startTBeans( testCase,
                             testResult );

        try {
            manager.waitForTBeans( testCase,
                                   3000 );
            fail( "should have thrown WatchdogException" );
        } catch (WatchdogException e) {
            assertEquals( 3000,
                          e.getTimeout() );

            assertLength( 1,
                          e.getTBeanIds() );

            assertContainsEquals( "One",
                                  e.getTBeanIds() );
        }
    }

    public void testWaitForTBeans_Double_WithTimeout_SingleExpiration()
        throws Throwable {

        SleepTBean tbeanOne = new SleepTBean( 5000 );
        SleepTBean tbeanTwo = new SleepTBean( 50 );

        DoubleTBeanCase testCase = new DoubleTBeanCase( tbeanOne,
                                                        tbeanTwo);

        testCase.initializeFactories();

        LocalTBeanManager manager = new LocalTBeanManager();

        TestResult testResult = new TestResult();

        manager.startTBeans( testCase,
                             testResult );

        try {
            manager.waitForTBeans( testCase,
                                   3000 );
            fail( "should have thrown WatchdogException" );
        } catch (WatchdogException e) {
            assertEquals( 3000,
                          e.getTimeout() );

            assertLength( 1,
                          e.getTBeanIds() );

            assertContainsEquals( "One",
                                  e.getTBeanIds() );
        }
    }

    public void testWaitForTBeans_Double_WithTimeout_DoubleExpiration()
        throws Throwable {

        SleepTBean tbeanOne = new SleepTBean( 5000 );
        SleepTBean tbeanTwo = new SleepTBean( 5000 );

        DoubleTBeanCase testCase = new DoubleTBeanCase( tbeanOne,
                                                        tbeanTwo);

        testCase.initializeFactories();

        LocalTBeanManager manager = new LocalTBeanManager();

        TestResult testResult = new TestResult();

        manager.startTBeans( testCase,
                             testResult );

        try {
            manager.waitForTBeans( testCase,
                                   3000 );
            fail( "should have thrown WatchdogException" );
        } catch (WatchdogException e) {
            assertEquals( 3000,
                          e.getTimeout() );

            assertLength( 2,
                          e.getTBeanIds() );

            assertContainsEquals( "One",
                                  e.getTBeanIds() );

            assertContainsEquals( "Two",
                                  e.getTBeanIds() );
        }
    }

    public void testValidateTBeans_Successful()
        throws Throwable {

        MockTBean tbeanOne = new MockTBean();
        MockTBean tbeanTwo = new MockTBean();

        DoubleTBeanCase testCase = new DoubleTBeanCase( tbeanOne,
                                                        tbeanTwo);

        testCase.initializeFactories();

        LocalTBeanManager manager = new LocalTBeanManager();

        TestResult testResult = new TestResult();

        manager.startTBeans( testCase,
                             testResult );

        manager.waitForTBeans( testCase,
                               0 );

        assertTrue( tbeanOne.hasRun() );
        assertTrue( tbeanTwo.hasRun() );

        assertEquals( 0,
                      testResult.failureCount() );

        assertEquals( 0,
                      testResult.errorCount() );

        manager.validateTBeans( testCase,
                                testResult );

        assertEquals( 0,
                      testResult.failureCount() );

        assertEquals( 0,
                      testResult.errorCount() );
    }

    public void testValidateTBeans_AssertionFailure_InRun()
        throws Throwable {

        FailTBean tbeanOne = new FailTBean();
        FailTBean tbeanTwo = new FailTBean();

        DoubleTBeanCase testCase = new DoubleTBeanCase( tbeanOne,
                                                        tbeanTwo);

        testCase.initializeFactories();

        LocalTBeanManager manager = new LocalTBeanManager();

        TestResult testResult = new TestResult();

        manager.startTBeans( testCase,
                             testResult );

        manager.waitForTBeans( testCase,
                               0 );

        assertTrue( tbeanOne.hasRun() );
        assertTrue( tbeanTwo.hasRun() );

        assertEquals( 0,
                      testResult.failureCount() );

        assertEquals( 0,
                      testResult.errorCount() );

        manager.validateTBeans( testCase,
                                testResult );

        assertEquals( 2,
                      testResult.failureCount() );

        assertEquals( 0,
                      testResult.errorCount() );
    }

    public void testValidateTBeans_Error_InRun()
        throws Throwable {

        Exception exceptionOne = new Exception();
        Exception exceptionTwo = new Exception();

        ErrorTBean tbeanOne = new ErrorTBean( exceptionOne );
        ErrorTBean tbeanTwo = new ErrorTBean( exceptionTwo );

        DoubleTBeanCase testCase = new DoubleTBeanCase( tbeanOne,
                                                        tbeanTwo);

        testCase.initializeFactories();

        LocalTBeanManager manager = new LocalTBeanManager();

        TestResult testResult = new TestResult();

        manager.startTBeans( testCase,
                             testResult );

        manager.waitForTBeans( testCase,
                               0 );

        assertTrue( tbeanOne.hasRun() );
        assertTrue( tbeanTwo.hasRun() );

        assertEquals( 0,
                      testResult.failureCount() );

        assertEquals( 0,
                      testResult.errorCount() );

        manager.validateTBeans( testCase,
                                testResult );

        assertEquals( 0,
                      testResult.failureCount() );

        assertEquals( 2,
                      testResult.errorCount() );

        assertContainsThrowable( exceptionOne,
                                 testResult.errors() );

        assertContainsThrowable( exceptionTwo,
                                 testResult.errors() );
    }

    public void testValidateTBeans_ErrorAndFailure_InRun()
        throws Throwable {

        Exception exceptionOne = new Exception();

        ErrorTBean tbeanOne = new ErrorTBean( exceptionOne );
        FailTBean tbeanTwo = new FailTBean();

        DoubleTBeanCase testCase = new DoubleTBeanCase( tbeanOne,
                                                        tbeanTwo);

        testCase.initializeFactories();

        LocalTBeanManager manager = new LocalTBeanManager();

        TestResult testResult = new TestResult();

        manager.startTBeans( testCase,
                             testResult );

        manager.waitForTBeans( testCase,
                               0 );

        assertTrue( tbeanOne.hasRun() );
        assertTrue( tbeanTwo.hasRun() );

        assertEquals( 0,
                      testResult.failureCount() );

        assertEquals( 0,
                      testResult.errorCount() );

        manager.validateTBeans( testCase,
                                testResult );

        assertEquals( 1,
                      testResult.failureCount() );

        assertEquals( 1,
                      testResult.errorCount() );

        assertContainsThrowable( exceptionOne,
                                 testResult.errors() );
    }

    // -- 

    public void testValidateTBeans_AssertionFailure_InAssertValid()
        throws Throwable {

        ValidationFailTBean tbeanOne = new ValidationFailTBean();
        ValidationFailTBean tbeanTwo = new ValidationFailTBean();

        DoubleTBeanCase testCase = new DoubleTBeanCase( tbeanOne,
                                                        tbeanTwo);

        testCase.initializeFactories();

        LocalTBeanManager manager = new LocalTBeanManager();

        TestResult testResult = new TestResult();

        manager.startTBeans( testCase,
                             testResult );

        manager.waitForTBeans( testCase,
                               0 );

        assertTrue( tbeanOne.hasRun() );
        assertTrue( tbeanTwo.hasRun() );

        assertEquals( 0,
                      testResult.failureCount() );

        assertEquals( 0,
                      testResult.errorCount() );

        manager.validateTBeans( testCase,
                                testResult );

        assertEquals( 2,
                      testResult.failureCount() );

        assertEquals( 0,
                      testResult.errorCount() );
    }

    public void testValidateTBeans_Error_InAssertValid()
        throws Throwable {

        Exception exceptionOne = new Exception();
        Exception exceptionTwo = new Exception();

        ValidationErrorTBean tbeanOne = new ValidationErrorTBean( exceptionOne );
        ValidationErrorTBean tbeanTwo = new ValidationErrorTBean( exceptionTwo );

        DoubleTBeanCase testCase = new DoubleTBeanCase( tbeanOne,
                                                        tbeanTwo);

        testCase.initializeFactories();

        LocalTBeanManager manager = new LocalTBeanManager();

        TestResult testResult = new TestResult();

        manager.startTBeans( testCase,
                             testResult );

        manager.waitForTBeans( testCase,
                               0 );

        assertTrue( tbeanOne.hasRun() );
        assertTrue( tbeanTwo.hasRun() );

        assertEquals( 0,
                      testResult.failureCount() );

        assertEquals( 0,
                      testResult.errorCount() );

        manager.validateTBeans( testCase,
                                testResult );

        assertEquals( 0,
                      testResult.failureCount() );

        assertEquals( 2,
                      testResult.errorCount() );

        assertContainsThrowable( exceptionOne,
                                 testResult.errors() );

        assertContainsThrowable( exceptionTwo,
                                 testResult.errors() );
    }

    public void testValidateTBeans_ErrorAndFailure_InAssertValid()
        throws Throwable {

        Exception exceptionOne = new Exception();

        ValidationErrorTBean tbeanOne = new ValidationErrorTBean( exceptionOne );
        ValidationFailTBean tbeanTwo = new ValidationFailTBean();

        DoubleTBeanCase testCase = new DoubleTBeanCase( tbeanOne,
                                                        tbeanTwo);

        testCase.initializeFactories();

        LocalTBeanManager manager = new LocalTBeanManager();

        TestResult testResult = new TestResult();

        manager.startTBeans( testCase,
                             testResult );

        manager.waitForTBeans( testCase,
                               0 );

        assertTrue( tbeanOne.hasRun() );
        assertTrue( tbeanTwo.hasRun() );

        assertEquals( 0,
                      testResult.failureCount() );

        assertEquals( 0,
                      testResult.errorCount() );

        manager.validateTBeans( testCase,
                                testResult );

        assertEquals( 1,
                      testResult.failureCount() );

        assertEquals( 1,
                      testResult.errorCount() );

        assertContainsThrowable( exceptionOne,
                                 testResult.errors() );
    }

    public void assertContainsThrowable(Throwable expected,
                                        Enumeration enum) {

        List items = new ArrayList();

        while ( enum.hasMoreElements() ) {
            TestFailure each = (TestFailure) enum.nextElement();
            Throwable thrown = each.thrownException();

            if ( thrown == expected ) {
                return;
            } else {
                items.add( thrown );
            }
        }

        fail( expected + " not contained in " + items );
    }
}
