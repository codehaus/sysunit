package org.sysunit.mesh;

import org.sysunit.SynchronizableTBean;
import org.sysunit.TBean;
import org.sysunit.TBeanSynchronizer;
import org.sysunit.model.TBeanInfo;
import org.sysunit.util.Barrier;
import org.sysunit.util.Blocker;

public class TBeanThread
    extends Thread {

    public static final TBeanThread[] EMPTY_ARRAY = new TBeanThread[0];

    private TBeanInfo tbeanInfo;
    private TBean tbean;

    private Barrier beginBarrier;
    private Blocker blocker;
    private Barrier endBarrier;

    private Throwable error;
    private boolean isDone;

    public TBeanThread(TBeanInfo tbeanInfo,
                       TBean tbean,
                       Barrier beginBarrier,
                       Blocker blocker,
                       Barrier endBarrier) {

        super( "tbean-thread." + tbeanInfo.getId() );

        this.tbeanInfo    = tbeanInfo;
        this.tbean        = tbean;

        this.beginBarrier = beginBarrier;
        this.blocker      = blocker;
        this.endBarrier   = endBarrier;

        this.error        = null;
        this.isDone       = false;
    }

    public TBeanInfo getTBeanInfo() {
        return this.tbeanInfo;
    }

    public TBean getTBean() {
        return this.tbean;
    }

    protected Barrier getBeginBarrier() {
        return this.beginBarrier;
    }

    protected Blocker getBlocker() {
        return this.blocker;
    }

    protected Barrier getEndBarrier() {
        return this.endBarrier;
    }
    
    public void run() {

        try {
            getBeginBarrier().block();
        } catch (InterruptedException e) {
            this.error = e;
            return;
        }

        try {
            getBlocker().block();
        } catch (InterruptedException e) {
            this.error = e;
            return;
        }

        try {
            tbean.setUp();
            tbean.run();
        } catch (Throwable e) {
            setError( e );
        } finally {
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
