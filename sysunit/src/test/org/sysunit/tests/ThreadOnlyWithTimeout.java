package org.sysunit.tests;

public class ThreadOnlyWithTimeout
    extends WatchedSystemTestCase
{
    public void threadOne()
        throws Exception
    {
        touch( "threadOne() a" );
        Thread.sleep( 3000 );
        touch( "threadOne() b" );
    }

    public void threadTwo()
        throws Exception
    {
        touch ( "threadTwo() a" );
        Thread.sleep( 3000 );
        touch( "threadTwo() b" );
    }

    public long getTimeout()
    {
        return 1000;
    }
}
