package org.sysunit.tests;

public class ThreadOnlyWithSync_Cascading
    extends WatchedSystemTestCase
{
    public void threadOne()
        throws Exception
    {
        touch( "threadOne() a" );
        sync( "one" );
        touch( "threadOne() b" );
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

    public void threadThree()
        throws Exception
    {
        touch( "threadThree() a" );
        sync( "one" );
        touch( "threadThree() b" );
        sync( "two" );
        touch( "threadThree() c" );
        sync( "three" );
        touch( "threadThree() d" );
    }
}
