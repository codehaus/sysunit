package org.sysunit.sync;

public interface SynchronizerCallback
{
    void notifyFullyBlocked(Synchronizer synchornizer);
    void notifyInconsistent(Synchronizer synchronizer);
}
