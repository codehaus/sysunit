package org.sysunit;

import java.util.List;
import java.util.ArrayList;

public class MockTBeanSynchronizer
    implements TBeanSynchronizer {

    private List syncPoints;

    public MockTBeanSynchronizer() {
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
