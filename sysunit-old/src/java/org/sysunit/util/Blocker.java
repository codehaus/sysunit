package org.sysunit.util;

public class Blocker {

    private boolean isBlocked;

    public Blocker() {
        this.isBlocked = true;
    }

    public synchronized void block()
        throws InterruptedException {

        while ( this.isBlocked ) {
            wait();
        }
    }

    public synchronized void unblock() {
        this.isBlocked = false;
        notifyAll();
    }
        
}
