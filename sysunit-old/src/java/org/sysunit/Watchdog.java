package org.sysunit;

import java.util.Timer;
import java.util.TimerTask;

class Watchdog extends TimerTask {

    private Watched watched;

    Watchdog(Watched watched,
             long timeout) {

        Timer timer = new Timer( true );

        timer.schedule( this,
                        timeout );

        this.watched = watched;
    }

    private Watched getWatched() {
        return this.watched;
    }

    public void run() {
        getWatched().triggerTimeout();
    }

}
