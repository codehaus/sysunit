package org.sysunit.command.master;

public class WatchdogThread
    extends Thread {

    private long timeout;
    private RunState state;
    private boolean cancelled;

    public WatchdogThread(long timeout,
                          RunState state) {
        this.timeout = timeout;
        this.state   = state;
    }

    public void cancel() {
        this.cancelled = true;
        interrupt();
    }

    public void run() {
        long start = System.currentTimeMillis();

        while ( ! this.cancelled ) {
            try {
                Thread.sleep( 500 );
            } catch (InterruptedException e) {
                break;
            }

            if ( this.cancelled ) {
                break;
            }

            if ( this.timeout > 0 ) {
                long now = System.currentTimeMillis();
                long diff = now - start;

                if ( diff >= this.timeout ) {
                    break;
                }
            }
        }
    }

}
