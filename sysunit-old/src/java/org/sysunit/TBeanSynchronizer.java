package org.sysunit;


public class TBeanSynchronizer {
    
    private String tbeanId;
    private Synchronizer synchronizer;

    public TBeanSynchronizer(String tbeanId,
                             Synchronizer synchronizer) {
        this.tbeanId      = tbeanId;
        this.synchronizer = synchronizer;
    }

    public String getTBeanId() {
        return this.tbeanId;
    }

    public Synchronizer getSynchronizer() {
        return this.synchronizer;
    }

    public void sync(String syncPoint)
        throws SynchronizationException, InterruptedException {
        getSynchronizer().sync( getTBeanId(),
                                syncPoint );
    }

}
