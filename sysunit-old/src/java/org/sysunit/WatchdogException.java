package org.sysunit;

import java.util.Arrays;

public class WatchdogException
    extends Exception {

    private long timeout;
    private String[] tbeanIds;

    public WatchdogException(long timeout,
                             String[] tbeanIds) {
        super( timeout + " ms watchdog expired for " + Arrays.asList( tbeanIds ) );
        this.timeout  = timeout;
        this.tbeanIds = tbeanIds;
    }

    public long getTimeout() {
        return this.timeout;
    }

    public String[] getTBeanIds() {
        return this.tbeanIds;
    }
}
