package org.sysunit.util;

import java.util.LinkedList;

public class ThreadPool
{
    private int numThreads;
    private Thread[] threads;

    private TaskQueue taskQueue;

    private Barrier startBarrier;
    private Barrier stopBarrier;

    public ThreadPool(int numThreads)
    {
        this.numThreads = numThreads;
        this.threads    = new Thread[ this.numThreads ];
        this.taskQueue  = new TaskQueue();

        this.startBarrier = new Barrier( numThreads + 1 );
        this.stopBarrier  = new Barrier( numThreads + 1 );
    }

    public synchronized void start()
        throws Exception
    {
        for ( int i = 0 ; i < this.threads.length ; ++i )
        {
            this.threads[ i ] = new TaskThread( "TaskThread-" + i,
                                                this.startBarrier,
                                                this.stopBarrier,
                                                this.taskQueue );
            
            this.threads[ i ].setDaemon( true );
            this.threads[ i ].start();
        }

        this.startBarrier.block();
    }

    public synchronized void stop()
        throws InterruptedException
    {
        for ( int i = 0 ; i < this.threads.length ; ++i )
        {
            threads[ i ].interrupt();
        }

        this.stopBarrier.block();
    }

    public int getQueueSize()
    {
        return this.taskQueue.size();
    }

    public void addTask(Runnable task)
    {
        this.taskQueue.addTask( task );
    }
}
