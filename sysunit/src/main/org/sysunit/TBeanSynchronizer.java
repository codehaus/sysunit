package org.sysunit;

import org.sysunit.sync.Synchronizer;

public class TBeanSynchronizer
{
    private Synchronizer synchronizer;

    public TBeanSynchronizer(Synchronizer synchronizer)
    {
        this.synchronizer = synchronizer;
    }

    public void sync(String syncPoint)
        throws SynchronizationException, InterruptedException
    {
        this.synchronizer.sync( syncPoint,
                                Thread.currentThread().getName() );
    }

}
