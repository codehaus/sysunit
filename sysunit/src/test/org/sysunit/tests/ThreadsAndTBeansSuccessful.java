package org.sysunit.tests;

import org.sysunit.TBean;

public class ThreadsAndTBeansSuccessful
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
        touch( "threadOne()" );
    }

    public void threadTwo()
        throws Exception
    {
        touch( "threadTwo()" );
    }

    public TBean tbeanOne()
        throws Exception
    {
        return newTBean( "tbeanOne" );
    }

    public TBean tbeanTwo()
        throws Exception
    {
        return newTBean( "tbeanTwo" );
    }
}
