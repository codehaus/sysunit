package org.sysunit.util;

import java.io.File;

public class JvmExecutorTest
    extends UtilTestBase
    implements JvmExecutorCallback
{
    private boolean done;
    private int exitValue;
    private Exception exception;
    private boolean interrupted;

    public void setUp()
    {
        this.done        = false;
        this.exitValue   = 0;
        this.exception   = null;
        this.interrupted = false;
    }
    
    public void testNoJavaHomeValidClass()
        throws Exception
    {
        JvmExecutor exec = new JvmExecutor( null,
                                            NoOpMain.class.getName(),
                                            new String[0],
                                            this );

        exec.run();

        waitFor();

        assertExitValue( 0 );
    }

    public void testJavaHomeValidClass()
        throws Exception
    {
        JvmExecutor exec = new JvmExecutor( new File( System.getProperty( "java.home" ) ),
                                            NoOpMain.class.getName(),
                                            new String[0],
                                            this );

        exec.run();

        waitFor();

        assertExitValue( 0 );
    }

    public void testInvalidClass()
        throws Exception
    {
        JvmExecutor exec = new JvmExecutor( new File( System.getProperty( "java.home" ) ),
                                            "spoon",
                                            new String[0],
                                            this );

        exec.run();

        waitFor();

        assertExitValueNot( 0 );
    }

    public void testArguments()
        throws Exception
    {
        JvmExecutor exec = new JvmExecutor( new File( System.getProperty( "java.home" ) ),
                                            ExitValueMain.class.getName(),
                                            new String[] { "44", "33" },
                                            this );

        exec.run();

        waitFor();

        assertExitValue( 77 );

    }

    public void testInterrupt()
        throws Exception
    {
        JvmExecutor exec = new JvmExecutor( new File( System.getProperty( "java.home" ) ),
                                            InfiniteMain.class.getName(),
                                            new String[0],
                                            this );

        Thread thread = new Thread( exec );

        thread.start();

        Thread.sleep( 2000 );

        thread.interrupt();

        waitFor();

        assertInterrupted();
    }

    synchronized void waitFor()
        throws InterruptedException
    {
        while ( ! this.done )
        {
            wait();
        }
    }

    public synchronized void notifyJvmFinished(JvmExecutor executor,
                                               int exitValue)
    {
        this.done = true;
        this.exitValue = exitValue;
        notifyAll();
    }

    public synchronized void notifyJvmInterrupted(JvmExecutor executor)
    {
        this.done = true;
        this.interrupted = true;
        notifyAll();
    }

    public synchronized void notifyJvmException(JvmExecutor executor,
                                                Exception e)
    {
        this.done = true;
        this.exception = e;
        notifyAll();
    }

    void assertExitValue(int expected)
    {
        if ( expected != this.exitValue )
        {
            fail( "expected exit value of <" + expected + "> but got <" + this.exitValue + ">" );
        }
    }

    void assertExitValueNot(int notExpected)
    {
        if ( notExpected == this.exitValue )
        {
            fail( "expected exit value not <" + notExpected + "> but it was" );
        }
    }

    void assertInterrupted()
    {
        if ( ! this.interrupted )
        {
            fail( "expected jvm to be interrupted" );
        }
    }
}
