package org.sysunit;

public interface TBean
{
    public void setUp()
        throws Throwable;

    public void run()
        throws Throwable;

    public void assertValid()
        throws Throwable;

    public void tearDown()
        throws Throwable;
}
