package org.sysunit;

public class MockTBean
    extends AbstractTBean {

    private boolean hasRun;

    public MockTBean() {
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
