package org.sysunit;

import java.util.ArrayList;
import java.util.List;

public class MockTBeanSynchronizer
    extends TBeanSynchronizer {

    private List syncPoints;

    public MockTBeanSynchronizer() {
        super( null,
               null );
        this.syncPoints = new ArrayList();
    }

    public void sync(String syncPoint)
        throws SynchronizationException, InterruptedException {

        this.syncPoints.add( syncPoint );
    }

    public String[] getSyncPoints() {
        return (String[]) this.syncPoints.toArray( new String[0] );
    }
}
