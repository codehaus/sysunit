package org.sysunit;

public class SleepTBean
    extends AbstractTBean {

    private long sleepTime;
    private boolean hasRun;

    public SleepTBean(long sleepTime) {
        this.sleepTime = sleepTime;
        this.hasRun = false;
    }

    public void run()
        throws Exception {
        Thread.sleep( this.sleepTime );
        this.hasRun = true;
    }

    public boolean hasRun() {
        return this.hasRun;
    }
}
