package org.sysunit.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Checkpoint {

    private static final Log log = LogFactory.getLog(Checkpoint.class);

    private String name;
    private int passed;
    private int numThreads;
    private CheckpointCallback callback;

    public Checkpoint(String name,
                      int numThreads,
                      CheckpointCallback callback) {
        this.name       = name;
        this.numThreads = numThreads;
        this.passed     = 0;
        this.callback   = callback;
    }

    public String getName() {
        return this.name;
    }

    public synchronized void pass()
        throws Exception {
        ++this.passed;

        log.debug( "* * * checkpoint " + getName() + " pass " + this.passed + " of " + this.numThreads );

        if ( this.passed == this.numThreads ) {
            notifyCallback();
        }
    }

    public void notifyCallback()
        throws Exception {
        if ( this.callback != null ) {
            this.callback.notify( this );
        }
    }

    public CheckpointCallback getCallback() {
        return this.callback;
    }

}
