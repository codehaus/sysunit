package org.sysunit.util;

import org.sysunit.SynchronizableTBean;
import org.sysunit.TBean;
import org.sysunit.TBeanSynchronizer;
import org.sysunit.Synchronizer;
import org.sysunit.SynchronizationException;

public class TBeanThread
    extends Thread {

    public static final TBeanThread[] EMPTY_ARRAY = new TBeanThread[0];

    private String tbeanId;
    private TBean tbean;
    private Synchronizer synchronizer;
    private Barrier beginBarrier;
    private Blocker blocker;
    private Barrier endBarrier;
    private Throwable error;
    private boolean isDone;

    public TBeanThread(String tbeanId,
                       TBean tbean,
                       Synchronizer synchronizer,
                       Barrier beginBarrier,
                       Blocker blocker,
                       Barrier endBarrier) {
        super( "tbean-thread." + tbeanId );
        this.tbeanId      = tbeanId;
        this.tbean        = tbean;
        this.synchronizer = synchronizer;
        this.beginBarrier = beginBarrier;
        this.blocker      = blocker;
        this.endBarrier   = endBarrier;
        this.isDone       = false;
    }

    public String getTBeanId() {
        return this.tbeanId;
    }

    public TBean getTBean() {
        return this.tbean;
    }

    public Synchronizer getSynchronizer() {
        return this.synchronizer;
    }

    public Barrier getBeginBarrier() {
        return this.beginBarrier;
    }

    public Barrier getEndBarrier() {
        return this.endBarrier;
    }

    public Blocker getBlocker() {
        return this.blocker;
    }
    
    public void run() {
        try {
            getBeginBarrier().block();
        } catch (InterruptedException e) {
            return;
        }

        try {
            getBlocker().block();
        } catch (InterruptedException e) {
            return;
        }

        try {
            tbean.setUp();
            tbean.run();
        } catch (Throwable e) {
            setError( e );
            e.printStackTrace();
            getSynchronizer().error( getTBeanId() );
        } finally {
            if ( getTBean() instanceof SynchronizableTBean ) {
                try {
                    getSynchronizer().unregisterSynchronizableTBean( getTBeanId() );
                } catch (SynchronizationException e) {
                    setError( e );
                }
            }
            synchronized ( this ) {
                this.isDone = true;
            }
            try {
                tbean.tearDown();
            } catch (Throwable t) {
                setError( t );
            }
            try {
                getEndBarrier().block();
            } catch (InterruptedException e) {
                // swallow and exit
            }
        }
    }

    public synchronized boolean isDone() {
        if ( ! isAlive() ) {
            return false;
        }

        return this.isDone;
    }

    public boolean hasError() {
        return ( this.error != null );
    }

    public Throwable getError() {
        return this.error;
    }

    protected void setError(Throwable error) {
        this.error = error;
    }
}
