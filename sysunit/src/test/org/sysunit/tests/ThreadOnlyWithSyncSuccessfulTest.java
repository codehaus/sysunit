package org.sysunit.tests;

public class ThreadOnlyWithSyncSuccessfulTest
    extends WatchedSystemTestCase
{
    public void setUp()
        throws Exception
    {
        super.setUp();
        touch( "setUp()" );
    }

    public void tearDown()
        throws Exception
    {
        touch( "tearDown()" );
        super.tearDown();
    }

    public void assertValid()
        throws Exception
    {
        super.assertValid();
        touch( "assertValid()" );
    }

    public void threadOne()
        throws Exception
    {
        touch( "threadOne() before sync" );
        sync( "one" );
        touch( "threadOne() after sync" );
    }

    public void threadTwo()
        throws Exception
    {
        touch( "threadTwo() before sync" );
        sync( "one" );
        touch( "threadTwo() after sync" );
    }
}
