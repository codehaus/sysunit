package org.sysunit.tests;

import org.sysunit.TBean;

public class ThrowingTBean
    implements TBean
{
    public void setUp()
        throws Exception
    {

    }

    public void tearDown()
        throws Exception
    {

    }

    public void assertValid()
        throws Exception
    {

    }

    public void run()
        throws Exception
    {
        throw new Exception( "intentional throw" );
    }
}
