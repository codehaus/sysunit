package org.sysunit.tests;

public class ThreadOnlyWithSync_Errors
    extends WatchedSystemTestCase
{
    public void threadOne()
        throws Exception
    {
        touch( "threadOne() a" );
        sync( "one" );
        touch( "threadOne() b" );
        throw new Exception();
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
