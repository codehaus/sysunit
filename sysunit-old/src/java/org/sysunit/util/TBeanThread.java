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
    private Checkpoint beginCheckpoint;
    private Blocker beginBlocker;
    private Checkpoint endCheckpoint;
    private Blocker endBlocker;
    private Checkpoint doneCheckpoint;
    private Throwable error;
    private boolean isDone;

    public TBeanThread(String tbeanId,
                       TBean tbean,
                       Synchronizer synchronizer,
                       Checkpoint beginCheckpoint,
                       Blocker beginBlocker,
                       Checkpoint endCheckpoint,
                       Blocker endBlocker,
                       Checkpoint doneCheckpoint) {
        super( "tbean-thread." + tbeanId );
        this.tbeanId         = tbeanId;
        this.tbean           = tbean;
        this.synchronizer    = synchronizer;
        this.beginCheckpoint = beginCheckpoint;
        this.beginBlocker    = beginBlocker;
        this.endCheckpoint   = endCheckpoint;
        this.endBlocker      = endBlocker;
        this.doneCheckpoint  = doneCheckpoint;
        this.isDone          = false;
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

    public Checkpoint getBeginCheckpoint() {
        return this.beginCheckpoint;
    }

    public Blocker getBeginBlocker() {
        return this.beginBlocker;
    }

    public Checkpoint getEndCheckpoint() {
        return this.endCheckpoint;
    }

    public Blocker getEndBlocker() {
        return this.endBlocker;
    }

    public Checkpoint getDoneCheckpoint() {
        return this.doneCheckpoint;
    }
    
    public void run() {
        try {
            try {
                tbean.setUp();
                getBeginCheckpoint().pass();
                getBeginBlocker().block();
            } catch (Exception e) {
                setError( e );
                return;
            }
            
            try {
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
                    getEndCheckpoint().pass();
                    getEndBlocker().block();
                    try {
                        tbean.tearDown();
                    } catch (Throwable t) {
                        setError( t );
                    }
                } catch (InterruptedException e) {
                    return;
                } catch (Exception e) {
                    return;
                }
            }
        } finally {
            try {
                getDoneCheckpoint().pass();
            } catch (Exception e) {
                return;
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
