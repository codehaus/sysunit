package org.sysunit.util;

public class TaskThread
    extends Thread
{
    private Barrier startBarrier;
    private Barrier stopBarrier;
    private TaskQueue queue;

    public TaskThread(String name,
                      Barrier startBarrier,
                      Barrier stopBarrier,
                      TaskQueue queue)
    {
        super( name );
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
            //System.err.println( this + " joining" );
            return;
        }

      //LOOP:
        while ( true )
        {
            try
            {
                Runnable task = this.queue.nextTask();
                task.run();
            }
            catch (InterruptedException e)
            {
                //break LOOP; 
            	break;
            }
        }

        try
        {
            this.stopBarrier.block();
        }
        catch (InterruptedException e)
        {
            //System.err.println( this + " joining" );
            return;
        }

        //System.err.println( this + " joining" );
    }
}
