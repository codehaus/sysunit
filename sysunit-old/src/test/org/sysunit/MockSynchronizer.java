package org.sysunit;

public class MockSynchronizer
    implements Synchronizer {

    private String tbeanId;
    private String syncPoint;
    private boolean unblockedAll;

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

    public void unblockAll() {
        this.unblockedAll = true;
    }

    public boolean unblockedAll() {
        return this.unblockedAll;
    }

    public void registerSynchronizableTBean(String tbeanId) {
    }

    public void unregisterSynchronizableTBean(String tbeanId) {
    }

    public void error(String tbeanId) {
    }
}
