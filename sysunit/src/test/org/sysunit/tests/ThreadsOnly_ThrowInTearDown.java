package org.sysunit.tests;

public class ThreadsOnly_ThrowInTearDown
    extends WatchedSystemTestCase
{
    public void setUp()
        throws Exception
    {
        touch( "setUp()" );
    }

    public void tearDown()
        throws Exception
    {
        touch( "tearDown()" );
        throw new Exception();
    }

    public void assertValid()
        throws Exception
    {
        touch( "assertValid()" );
    }

    public void threadOne()
        throws Exception
    {
        touch( "threadOne()" );
    }

    public void threadTwo()
        throws Exception
    {
        touch( "threadTwo()" );
    }
}
