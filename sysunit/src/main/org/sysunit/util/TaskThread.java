package org.sysunit.util;

public class TaskThread
    extends Thread
{
    private Barrier startBarrier;
    private Barrier stopBarrier;
    private TaskQueue queue;

    public TaskThread(Barrier startBarrier,
                      Barrier stopBarrier,
                      TaskQueue queue)
    {
        super( "TaskThread" );
        this.startBarrier = startBarrier;
        this.stopBarrier  = stopBarrier;
        this.queue        = queue;
    }

    public void run()
    {
        try
        {
            this.startBarrier.block();
        }
        catch (InterruptedException e)
        {
            return;
        }

      LOOP:
        while ( true )
        {
            try
            {
                Runnable task = this.queue.nextTask();
                task.run();
            }
            catch (InterruptedException e)
            {
                break LOOP;
            }
        }

        try
        {
            this.stopBarrier.block();
        }
        catch (InterruptedException e)
        {
            return;
        }
    }
}
