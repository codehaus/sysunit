package org.sysunit.tests;

import org.sysunit.TBean;

public class ThreadsAndTBeans_ThrowInSetUp
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
    {
        return new WatchedTBean( this,
                                 "tbeanOne" )
            {
                public void setUp()
                    throws Exception
                {
                    touch( "tbeanOne setUp()" );
                    throw new Exception();
                }
            };
    }

    public TBean tbeanTwo()
    {
        return new WatchedTBean( this,
                                 "tbeanTwo" )
            {
                public void setUp()
                    throws Exception
                {
                    touch( "tbeanTwo setUp()" );
                    throw new Exception();
                }
            };
    }
}
