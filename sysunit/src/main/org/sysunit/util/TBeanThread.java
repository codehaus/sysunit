package org.sysunit.util;

import org.sysunit.TBean;
import org.sysunit.ThreadMethodTBean;
import org.sysunit.sync.Synchronizer;
import org.sysunit.sync.SecondaryFailureError;

import java.lang.reflect.InvocationTargetException;

public class TBeanThread
    extends Thread
{
    private static final Throwable[] EMPTY_THROWABLE_ARRAY = new Throwable[0];
    private static final Runnable EXIT_TASK = new Runnable()
        {
            public void run() { }
        };

    private TBean tbean;
    private TBeanThreadCallback callback;
    private Runnable task;
    private Object taskLock;

    private Throwable thrown;

    public TBeanThread(TBean tbean,
                       TBeanThreadCallback callback)
    {
        super( ( tbean instanceof ThreadMethodTBean )
               ? ( ((ThreadMethodTBean)tbean).getMethod().getName() )
               : ( tbean.getClass().getName() ) );
        this.tbean    = tbean;
        this.callback = callback;
        this.task     = null;
        this.taskLock = new Object();
        this.thrown   = null;
    }

    public TBean getTBean()
    {
        return this.tbean;
    }

    public TBeanThreadCallback getCallback()
    {
        return this.callback;
    }

    void performTask(Runnable task)
        throws InterruptedException
    {
        synchronized ( this.taskLock )
        {
            while ( this.task != null )
            {
                this.taskLock.wait();
            }
            
            this.thrown = null;
            this.task = task;
            this.taskLock.notifyAll();
        }
    }

    void setThrown(Throwable thrown)
    {
        if ( thrown instanceof InvocationTargetException )
        {
            thrown = ((InvocationTargetException)thrown).getTargetException();
        }

        if ( thrown instanceof SecondaryFailureError )
        {
            return;
        }

        this.thrown = thrown;
    }

    public Throwable getThrown()
    {
        return this.thrown;
    }

    public void performSetUp()
        throws InterruptedException
    {
        performTask( new Runnable()
            {
                public void run()
                {
                    try
                    {
                        getTBean().setUp();
                    }
                    catch (Throwable t)
                    {
                        setThrown( t );
                    }
                    getCallback().notifySetUp( TBeanThread.this );
                }
            } );
    }

    public void performRun()
        throws InterruptedException
    {
        performTask( new Runnable()
            {
                public void run()
                {
                    TBean tbean = getTBean();

                    try
                    {
                        if ( tbean instanceof ThreadMethodTBean )
                        {
                            Synchronizer.setThreadSynchronizer( ((ThreadMethodTBean)tbean).getSynchronizer() );
                        }

                        tbean.run();

                    }
                    catch (Throwable t)
                    {
                        setThrown( t );
                    }
                    finally
                    {
                        if ( tbean instanceof ThreadMethodTBean )
                        {
                            Synchronizer.setThreadSynchronizer( null );
                        }
                    }

                    getCallback().notifyRun( TBeanThread.this );
                }
            } );
    }

    public void performAssertValid()
        throws InterruptedException
    {
        performTask( new Runnable()
            {
                public void run()
                {
                    try
                    {
                        getTBean().assertValid();
                    }
                    catch (Throwable t)
                    {
                        setThrown( t );
                    }
                    getCallback().notifyAssertValid( TBeanThread.this );
                }
            } );
    }

    public void performTearDown()
        throws InterruptedException
    {
        performTask( new Runnable()
            {
                public void run()
                {
                    try
                    {
                        getTBean().tearDown();
                    }
                    catch (Throwable t)
                    {
                        setThrown( t );
                    }
                    getCallback().notifyTearDown( TBeanThread.this );
                }
            } );
    }

    public void perfromStop()
        throws InterruptedException
    {
        performTask( EXIT_TASK );
    }

    public void run()
    {
      SERVICE_LOOP:
        while ( true )
        {
            try
            {
                synchronized ( this.taskLock )
                {
                    while ( this.task == null )
                    {
                        this.taskLock.wait();
                    }
                    
                    if ( this.task == EXIT_TASK )
                    {
                        break SERVICE_LOOP;
                    }
                    
                    this.task.run();
                    this.task = null;
                    this.taskLock.notifyAll();
                }
            }
            catch (InterruptedException e)
            {
                break SERVICE_LOOP;
            }
        }
    }
}
