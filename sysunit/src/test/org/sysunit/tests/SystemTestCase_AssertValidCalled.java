package org.sysunit.tests;

public class SystemTestCase_AssertValidCalled
    extends WatchedSystemTestCase
{

    protected static int count = 0;

    public void setUp()
        throws Exception
    {
        count++;
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
	if( count == 1 ) {
	    throw new RuntimeException("Throwing first exception");
	}
    }

    public void threadTwo()
        throws Exception
    {
        touch( "threadTwo()" );
    }

}
