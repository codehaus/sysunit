package org.sysunit.util;

public class Barrier {

    private int numThreads;
    private int numBlocked;

    public Barrier(int numThreads) {
        this.numThreads = numThreads;
    }

    public int getNumThreads() {
        return this.numThreads;
    }

    public synchronized void block()
        throws InterruptedException {
        ++this.numBlocked;

        while ( this.numBlocked < this.numThreads ) {
            wait();
        }

        notifyAll();
    }
}
