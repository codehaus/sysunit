package org.sysunit.tests;

import org.sysunit.TBean;

public class ThreadsAndTBeansWithSyncSuccessful
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
        touch( "threadOne() start" );
        sync( "a" );
        sync( "b" );
        touch( "threadOne() end" );
    }

    public void threadTwo()
        throws Exception
    {
        touch( "threadTwo() start" );

        sync( "a" );
        sync( "b" );

        touch( "threadTwo() end" );
    }

    public TBean tbeanOne()
        throws Exception
    {
        return newSyncTBean( "tbeanOne",
                             new String[] { "a", "b" } );
    }

    public TBean tbeanTwo()
        throws Exception
    {
        return newSyncTBean( "tbeanTwo",
                             new String[] { "a", "b" } );
    }
}
