package org.sysunit.util;

public class TaskQueueTest
    extends UtilTestBase
{

    private Runnable task;


    public void setUp()
    {
        this.task = null;
    }

    public void tearDown()
    {
        this.task = null;
    }

    public void testAddAndNext()
        throws Exception
    {
        final Runnable theTask = new Runnable()
            {
                public void run()
                {

                };
            };

        final TaskQueue queue = new TaskQueue();

        Thread addTaskThread = new Thread()
            {
                public void run()
                {
                    queue.addTask( theTask );
                }
            };

        Thread nextTaskThread = new Thread()
            {
                public void run()
                {
                    try
                    {
                        setTask( queue.nextTask() );
                    }
                    catch (InterruptedException e)
                    {
                        fail( "caught InterruptedException" );
                    }
                }
            };

        nextTaskThread.start();

        Thread.sleep( 1000 );

        assertNull( "not task fetched",
                    getTask() );

        addTaskThread.start();

        Thread.sleep( 1000 );

        assertSame( theTask,
                    this.task );
    }

    public void testInterrupt()
        throws Exception
    {
        final Runnable theTask = new Runnable()
            {
                public void run()
                {

                };
            };

        final TaskQueue queue = new TaskQueue();

        Thread addTaskThread = new Thread()
            {
                public void run()
                {
                    queue.addTask( theTask );
                }
            };

        Thread nextTaskThread = new Thread()
            {
                public void run()
                {
                    try
                    {
                        queue.nextTask();
                        fail( "should have caught InterruptedException" );
                    }
                    catch (InterruptedException e)
                    {
                        // expected and correct;
                    }
                }
            };

        nextTaskThread.start();

        Thread.sleep( 1000 );

        assertNull( "not task fetched",
                    getTask() );

        nextTaskThread.interrupt();

        addTaskThread.start();

        Thread.sleep( 1000 );

        assertNull( "not task fetched",
                    getTask() );
    }

    void setTask(Runnable task)
    {
        this.task = task;
    }

    Runnable getTask()
    {
        return this.task;
    }
}
