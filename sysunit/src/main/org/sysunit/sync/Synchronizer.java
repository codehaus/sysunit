package org.sysunit.sync;

import org.sysunit.TBeanSynchronizer;
import org.sysunit.ThreadMethodTBean;
import org.sysunit.InconsistentSyncException;

public class Synchronizer
{
    private static ThreadLocal synchronizer = new ThreadLocal();

    private int sequence;
    private int numThreads;
    private int waitingThreads;

    private String currentSyncPoint;
    private String inconsistentSyncer;

    private SynchronizerCallback callback;

    private boolean error;

    public Synchronizer(int numThreads,
                        SynchronizerCallback callback)
    {
        this.numThreads         = numThreads;
        this.sequence           = 0;
        this.inconsistentSyncer = null;

        this.callback           = callback;
    }

    public SynchronizerCallback getCallback()
    {
        return this.callback;
    }

    public synchronized void sync(String syncPoint,
                                  String syncer)
        throws InterruptedException, InconsistentSyncException
    {
        checkConsistency( syncPoint,
                          syncer );

        int localSequence = this.sequence;

        ++this.waitingThreads;

        checkBlockage();

        while ( localSequence == this.sequence )
        {
            if ( this.error )
            {
                throw new SecondaryFailureError();
            }

            wait();
        }

        if ( this.error )
        {
            throw new SecondaryFailureError();
        }

        if ( this.inconsistentSyncer != null )
        {
            throw new InconsistentSyncException( syncPoint, syncer );
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

    public synchronized void reduceNumThreads()
    {
        --this.numThreads;
        checkBlockage();
    }

    public synchronized void setError()
    {
        this.error = true;
        notifyAll();
    }

    void checkBlockage()
    {
        if ( this.waitingThreads == this.numThreads )
        {
            if ( this.numThreads > 0 )
            {
                getCallback().notifyFullyBlocked( this );
            }
        }
    }

    public synchronized void unblock()
    {
        this.currentSyncPoint = null;
        ++this.sequence;
        this.waitingThreads = 0;
        notifyAll();
    }

    void checkConsistency(String syncPoint,
                          String syncer)
        throws InconsistentSyncException
    {
        if ( this.currentSyncPoint == null )
        {
            this.currentSyncPoint = syncPoint;
        }

        if ( this.currentSyncPoint.equals( syncPoint ) )
        {
            return;
        }

        this.inconsistentSyncer = syncer;

        getCallback().notifyInconsistent( this );

        unblock();

        throw new InconsistentSyncException( this.currentSyncPoint,
                                             syncer,
                                             syncPoint );
    }

    static public void setThreadSynchronizer(TBeanSynchronizer synchronizer)
    {
        Synchronizer.synchronizer.set( synchronizer );
    }

    static public TBeanSynchronizer getThreadSynchronizer()
    {
        return (TBeanSynchronizer) Synchronizer.synchronizer.get();
    }
}
