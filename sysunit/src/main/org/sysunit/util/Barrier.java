package org.sysunit.util;

public class Barrier
{
    private int sequence;
    private int numThreads;
    private int waitingThreads;

    public Barrier(int numThreads)
    {
        this.numThreads = numThreads;
        this.sequence   = 0;
    }

    public synchronized void block()
        throws InterruptedException
    {
        int localSequence = this.sequence;

        ++this.waitingThreads;

        if ( this.waitingThreads == this.numThreads )
        {
            ++this.sequence;
            this.waitingThreads = 0;
            notifyAll();
        }
        else
        {
            while ( localSequence == this.sequence )
            {
                wait();
            }
        }
    }

    public synchronized int getNumWaitingThreads()
    {
        return this.waitingThreads;
    }

    public synchronized int getNumThreads()
    {
        return this.numThreads;
    }
}
