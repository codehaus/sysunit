package org.sysunit.builder;

import org.sysunit.SystemTestCase;

public class MostBasicTestCase
    extends SystemTestCase
{
    private long timeout;

    public MostBasicTestCase(long timeout)
    {
        this.timeout = timeout;
    }

    public long getTimeout()
    {
        return this.timeout;
    }
}
