package org.sysunit;

import java.util.Timer;
import java.util.TimerTask;

class Watchdog extends TimerTask {

    private Watched watched;
    private long timeout;

    Watchdog(Watched watched,
             long timeout) {
        
        this.watched = watched;
        this.timeout = timeout;

        if ( this.timeout > 0 ) {
            Timer timer = new Timer( true );
            
            timer.schedule( this,
                            timeout );
        }
    }

    long getTimeout() {
        return this.timeout;
    }

    Watched getWatched() {
        return this.watched;
    }

    public void run() {
        getWatched().triggerTimeout();
    }

}
