package org.sysunit.tests;

public class ThreadOnlyWithSync_Failures
    extends WatchedSystemTestCase
{
    public void threadOne()
        throws Exception
    {
        touch( "threadOne() a" );
        sync( "one" );
        touch( "threadOne() b" );
        assertEquals( 1,
                      2 );
        sync( "two" );
        touch( "threadOne() c" );
    }

    public void threadTwo()
        throws Exception
    {
        touch( "threadTwo() a" );
        sync( "one" );
        touch( "threadTwo() b" );
        sync( "two" );
        touch( "threadTwo() c" );
    }
}
