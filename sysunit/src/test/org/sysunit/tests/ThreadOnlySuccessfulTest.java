package org.sysunit.tests;

public class ThreadOnlySuccessfulTest
    extends WatchedSystemTestCase
{
    public static Object suite()
        throws Exception {
        return suite( ThreadOnlySuccessfulTest.class );
    }
    
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
}
