package org.sysunit;

public class MockSynchronizableTBean
    extends AbstractSynchronizableTBean {

    private boolean hasRun;

    public MockSynchronizableTBean() {
        this.hasRun = false;
    }

    public void run()
        throws Exception {
        this.hasRun = true;
    }

    public boolean hasRun() {
        return this.hasRun;
    }
}
