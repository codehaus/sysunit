package org.sysunit.tests;

public class ThreadsOnly_ThrowInSetUp
    extends WatchedSystemTestCase
{
    public void setUp()
        throws Exception
    {
        touch( "setUp()" );
        throw new Exception();
    }

    public void tearDown()
        throws Exception
    {
        touch( "tearDown()" );
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
