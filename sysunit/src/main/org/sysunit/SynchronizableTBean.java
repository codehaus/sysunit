package org.sysunit;

public interface SynchronizableTBean
    extends TBean
{
    void setSynchronizer(TBeanSynchronizer synchronizer);
}

