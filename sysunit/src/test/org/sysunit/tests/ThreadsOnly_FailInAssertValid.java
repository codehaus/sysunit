package org.sysunit.tests;

public class ThreadsOnly_FailInAssertValid
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
    }

    public void assertValid()
        throws Exception
    {
        touch( "assertValid()" );
        fail( "intentional failure" );
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
