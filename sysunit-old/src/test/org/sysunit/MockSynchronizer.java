package org.sysunit;

public class MockSynchronizer
    implements Synchronizer {

    private String tbeanId;
    private String syncPoint;

    public void sync(String tbeanId,
                     String syncPoint) {

        this.tbeanId = tbeanId;
        this.syncPoint = syncPoint;
    }

    public String getTBeanId() {
        return this.tbeanId;
    }

    public String getSyncPoint() {
        return this.syncPoint;
    }
}
