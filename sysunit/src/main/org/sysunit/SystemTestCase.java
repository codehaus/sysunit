package org.sysunit;

import org.sysunit.builder.SystemTestInfoBuilder;
import org.sysunit.model.SystemTestInfo;
import org.sysunit.model.ThreadInfo;
import org.sysunit.model.TBeanFactoryInfo;
import org.sysunit.util.TBeanThread;
import org.sysunit.util.TBeanThreadCallback;
import org.sysunit.sync.Synchronizer;
import org.sysunit.sync.SynchronizerCallback;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.framework.AssertionFailedError;

import java.io.InputStreamReader;
import java.io.BufferedReader;

public class SystemTestCase
    extends Assert
    implements Test, TBeanThreadCallback, SynchronizerCallback
{

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    private Thread testThread;
    private TestResult testResult;
    private int completed;

    private Synchronizer synchronizer;

    private TBeanThread[] threads;

    private boolean runComplete;
    
    public SystemTestCase()
    {
    }

    public void setUp()
        throws Exception
    {
    }

    public void tearDown()
        throws Exception
    {
    }

    public void assertValid()
        throws Exception
    {
    }

    public long getTimeout()
    {
        return 0;
    }

    /**
     * @see Test
     */
    public int countTestCases()
    {
        return 2;
    }

    /**
     * @see Test
     */
    public void run(TestResult testResult)
    {
        this.testResult = testResult;

        this.testResult.startTest( this );

        try
        {
            SystemTestInfo testInfo = SystemTestInfoBuilder.build( this );


            this.threads = initializeThreads( testInfo.getThreads(),
                                              testInfo.getTBeanFactories() );

            setUp();
            runTest( threads );

            if ( getTimeout() > 0 )
            {
                long start = System.currentTimeMillis();
                long stop  = start + getTimeout();

                synchronized ( this )
                {
                    while ( System.currentTimeMillis() < stop
                            &&
                            ! this.runComplete )
                    {
                        long now = System.currentTimeMillis();

                        long left = stop - now;
                        
                        wait( left );
                    }


                    if ( ! this.runComplete )
                    {
                        for ( int i = 0 ; i < this.threads.length ; ++i )
                        {
                            this.threads[ i ].interrupt();
                        }
                        
                        throw new WatchdogError();
                    }
                }
            }
        }
        catch (WatchdogError e)
        {
            this.testResult.addFailure( this,
                                        e );
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            this.testResult.addError( this,
                                      t );
        }
        finally
        {
            if ( this.testThread != null )
            {
                try
                {
                    this.testThread.join();
                }
                catch (InterruptedException e)
                {
                    this.testResult.addError( this,
                                              e );
                }
                finally
                {
                    try
                    {
                        if ( this.testResult.errorCount() +
                             this.testResult.failureCount() == 0 )
                        {
                            try
                            {
                                assertValid();
                            }
                            catch (AssertionFailedError e)
                            {
                                this.testResult.addFailure( this,
                                                            e );
                            }
                            catch (Exception e)
                            {
                                this.testResult.addError( this,
                                                          e );
                            }
                        }
                        
                        tearDown();
                    }
                    catch (Exception e)
                    {
                        this.testResult.addError( this,
                                                  e );
                    }
                }
            }
            this.testResult.endTest( this );
            this.testResult = null;
        }
    }

    TBeanThread[] initializeThreads(ThreadInfo[] threadInfos,
                                    TBeanFactoryInfo[] tbeanFactoryInfos)
        throws Exception
    {
        TBeanThread[] threads = new TBeanThread[ threadInfos.length + tbeanFactoryInfos.length ];
        
        for ( int i = 0 ; i < threadInfos.length ; ++i )
        {
            threads[ i ] = new TBeanThread( initializeTBean( threadInfos[ i ] ),
                                            this );
        }
        
        for ( int i = threadInfos.length ; i < threadInfos.length + tbeanFactoryInfos.length ; ++i )
        {
            threads[ i ] = new TBeanThread( initializeTBean( tbeanFactoryInfos[ i - threadInfos.length ] ),
                                            this );
        }

        int numSynchronized = 0;

        for ( int i = 0 ; i < threads.length ; ++i )
        {
            if ( threads[ i ].getTBean() instanceof SynchronizableTBean )
            {
                ++numSynchronized;
            }
        }

        this.synchronizer = new Synchronizer( numSynchronized,
                                              this );

        for ( int i = 0 ; i < threads.length ; ++i )
        {
            TBean tbean = threads[ i ].getTBean();

            if ( tbean instanceof SynchronizableTBean )
            {
                ((SynchronizableTBean)tbean).setSynchronizer( new TBeanSynchronizer( this.synchronizer ) );
            }
        }

        return threads;

    }

    void runTest(final TBeanThread[] threads)
    {
        this.testThread = new Thread()
            {
                public void run()
                {
                    for ( int i = 0 ; i < threads.length ; ++i )
                    {
                        threads[ i ].start();
                    }
                    
                    try
                    {
                        if ( performSetUp( threads ) )
                        {
                            if ( performRun( threads ) )
                            {
                                performAssertValid( threads );
                            }
                        }
                        
                        performTearDown( threads );
                    }
                    catch (InterruptedException e)
                    {
                        synchronized ( SystemTestCase.this )
                        {
                            SystemTestCase.this.testResult.addError( SystemTestCase.this,
                                                                     e );
                        }
                    }
                }
            };

        this.testThread.start();
    }

    /**
     * Synchronize a synthesized <code>ThreadMethodTBean</code> at
     * a sync-point.
     *
     * <p>
     * This method is usable <b>only</b> by reflected/synthesized
     * <code>ThreadMethodTBean</code> instances.  It replaces the
     * <code>sync(...)</code> method in <code>AbstractSynchronizableTBean</code>.
     * </p>
     *
     * <p>
     * This method relies upon <code>ThreadLocal</code> data-structures
     * that are configured by the test-runner.
     * </p>
     *
     * @see AbstractSynchronizableTBean
     *
     * @param syncPoint The sync-point.
     *
     * @throws SynchronizationException If an error occurs while attempting
     *         to perform synchronization.
     * @throws InterruptedException If the synchronization is interrupted.
     */
    protected void sync(String syncPoint)
        throws SynchronizationException, InterruptedException
    {
        Synchronizer.getThreadSynchronizer().sync( syncPoint );
    }

    /*
    public static Test suite(Class systemTestClass)
        throws Exception {
        TestSuite suite = new TestSuite();

        suite.addTest( (Test) systemTestClass.newInstance() );

        suite.setName( systemTestClass.getName() );

        return suite;
    }
    */

    TBean initializeTBean(ThreadInfo threadInfo)
        throws Exception
    {
        TBean tbean = new ThreadMethodTBean( threadInfo.getSystemTestCase(),
                                             threadInfo.getMethod() );

        return tbean;
    }

    TBean initializeTBean(TBeanFactoryInfo tbeanFactoryInfo)
        throws Exception
    {
        TBean tbean = (TBean) tbeanFactoryInfo.getMethod().invoke( tbeanFactoryInfo.getSystemTestCase(),
                                                                   EMPTY_OBJECT_ARRAY );

        return tbean;
    }

    boolean performSetUp(TBeanThread[] threads)
        throws InterruptedException
    {
        this.completed = 0;

        for ( int i = 0 ; i < threads.length ; ++i )
        {
            threads[ i ].performSetUp();
        }

        waitFor( threads );

        return ( this.testResult.failureCount() +
                 this.testResult.errorCount() == 0 );
    }

    boolean performRun(TBeanThread[] threads)
        throws InterruptedException
    {
        this.completed = 0;

        for ( int i = 0 ; i < threads.length ; ++i )
        {
            threads[ i ].performRun();
        }

        waitFor( threads );

        return ( this.testResult.failureCount() +
                 this.testResult.errorCount() == 0 );
    }

    boolean performAssertValid(TBeanThread[] threads)
        throws InterruptedException
    {
        this.completed = 0;

        for ( int i = 0 ; i < threads.length ; ++i )
        {
            threads[ i ].performAssertValid();
        }

        waitFor( threads );

        return ( this.testResult.failureCount() +
                 this.testResult.errorCount() == 0 );
    }

    boolean performTearDown(TBeanThread[] threads)
        throws InterruptedException
    {
        this.completed = 0;

        for ( int i = 0 ; i < threads.length ; ++i )
        {
            threads[ i ].performTearDown();
        }

        waitFor( threads );

        return ( this.testResult.failureCount() +
                 this.testResult.errorCount() == 0 );
    }

    public void notifySetUp(TBeanThread thread)
    {
        notifyStepComplete( thread );
    }

    public void notifyRun(TBeanThread thread)
    {
        synchronized ( this )
        {
            this.runComplete = true;
            notifyAll();
        }
        notifyStepComplete( thread );
        this.synchronizer.reduceNumThreads();
    }

    public void notifyAssertValid(TBeanThread thread)
    {
        notifyStepComplete( thread );
    }

    public void notifyTearDown(TBeanThread thread)
    {
        notifyStepComplete( thread );
    }

    public void notifyFullyBlocked(Synchronizer synchronizer)
    {
        /*
        System.err.println( "<enter> to continue past sync()" );

        try
        {
            BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
            
            reader.readLine();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        */

        synchronizer.unblock();
    }

    public synchronized void notifyInconsistent(Synchronizer synchronizer)
    {
        this.testResult.addError( this,
                                  new InconsistentSyncException( null, null ) );
    }

    synchronized void notifyStepComplete(TBeanThread thread)
    {
        Throwable thrown = thread.getThrown();

        if ( thrown != null )
        {
            if ( thrown instanceof AssertionFailedError )
            {
                this.testResult.addFailure( this,
                                            (AssertionFailedError) thrown );
            }
            else
            {
                this.testResult.addError( this,
                                          thrown );
            }
            this.synchronizer.setError();
        }

        ++this.completed;
        notifyAll();
    }

    synchronized void waitFor(TBeanThread[] threads)
        throws InterruptedException
    {
        while ( this.completed != threads.length )
        {
            wait();
        }
    }

    public static Test suite(Class systemTestClass)
        throws Exception {
        TestSuite suite = new TestSuite();

        suite.addTest( (Test) systemTestClass.newInstance() );

        suite.setName( systemTestClass.getName() );

        return suite;
    }
}

