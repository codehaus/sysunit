package org.sysunit;

public class WatchdogException
    extends Exception {

    private long timeout;

    public WatchdogException(long timeout) {
        this.timeout = timeout;
    }

    public long getTimeout() {
        return this.timeout;
    }

    public String getMessage() {
        return getTimeout() + "ms watchdog expired";
    }
}
