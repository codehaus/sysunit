package org.sysunit.util;

import java.util.LinkedList;

public class TaskQueue
{
    private LinkedList tasks;

    public TaskQueue()
    {
        this.tasks = new LinkedList();
    }

    public synchronized Runnable nextTask()
        throws InterruptedException
    {
        while ( this.tasks.isEmpty() )
        {
            wait();
        }

        return (Runnable) this.tasks.removeFirst();
    }

    public synchronized void addTask(Runnable task)
    {
        this.tasks.addLast( task );
        notifyAll();
    }

    public synchronized int size()
    {
        return this.tasks.size();
    }
}
