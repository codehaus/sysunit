package org.sysunit.util;

import java.util.List;
import java.util.ArrayList;

public class ThreadPoolTest
    extends UtilTestBase
{
    private List touches;

    public void setUp()
    {
        this.touches = new ArrayList();
    }

    public void tearDown()
    {
        this.touches = null;
    }

    public void testStartStop()
        throws Exception
    {
        ThreadPool pool = new ThreadPool( 2 );
                        
        pool.start();
        pool.stop();
    }

    public void testMultipleTasks()
        throws Exception
    {
        ThreadPool pool = new ThreadPool( 2 );

        pool.start();

        pool.addTask( new Runnable()
            {
                public void run()
                {
                    try
                    {
                        Thread.sleep( 3000 );
                    }
                    catch (InterruptedException e)
                    {
                        fail( e.getMessage() );
                    }

                    touch( Thread.currentThread() );
                }
            } );

        pool.addTask( new Runnable()
            {
                public void run()
                {
                    try
                    {
                        Thread.sleep( 3000 );
                    }
                    catch (InterruptedException e)
                    {
                        fail( e.getMessage() );
                    }

                    touch( Thread.currentThread() );
                }
            } );

        Thread.sleep( 5000 );

        pool.stop();

        assertEquals( "no tasks left in queue",
                      0,
                      pool.getQueueSize() );

        Object[] touches = getTouches();

        assertLength( "two touches",
                      2,
                      touches );

        assertNotEquals( "different threads",
                         touches[0],
                         touches[1] );
    }

    void touch(Object touch)
    {
        this.touches.add( touch );
    }

    Object[] getTouches()
    {
        return this.touches.toArray();
    }
}
