package org.sysunit;

public class InconsistentSyncException
    extends SynchronizationException
{
    private String syncPoint;
    private String invalidSyncPoint;
    private String invalidSyncer;

    public InconsistentSyncException(String syncPoint,
                                     String invalidSyncer)
    {
        this.syncPoint     = syncPoint;
        this.invalidSyncer = invalidSyncer;
    }

    public InconsistentSyncException(String syncPoint, 
                                     String invalidSyncer,
                                     String invalidSyncPoint)
    {
        this( syncPoint,
              invalidSyncer );

        this.invalidSyncPoint = invalidSyncPoint;
    }
}
