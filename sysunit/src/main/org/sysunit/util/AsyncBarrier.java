package org.sysunit.util;

public class AsyncBarrier
{
    private int numThreads;
    private int waitingThreads;

    private AsyncBarrierCallback[] callbacks;

    public AsyncBarrier(int numThreads)
    {
        this.numThreads     = numThreads;
        this.waitingThreads = 0;
        this.callbacks      = new AsyncBarrierCallback[ this.numThreads ];
    }

    public synchronized void block(AsyncBarrierCallback callback)
    {
        this.callbacks[ this.waitingThreads ] = callback;

        ++this.waitingThreads;

        if ( this.waitingThreads == this.numThreads )
        {
            this.waitingThreads = 0;

            for ( int i = 0 ; i < this.numThreads; ++i )
            {
                this.callbacks[ i ].unblock( this );
                this.callbacks[ i ] = null;
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
