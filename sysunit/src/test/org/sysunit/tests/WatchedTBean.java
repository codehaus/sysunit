package org.sysunit.tests;

import org.sysunit.TBean;

public class WatchedTBean
    implements TBean
{
    private WatchedSystemTestCase test;
    private String id;

    public WatchedTBean(WatchedSystemTestCase test,
                        String id)
    {
        this.test = test;
        this.id   = id;
    }

    public void setUp()
        throws Exception
    {
        this.test.touch( this.id + " setUp()" );
    }

    public void tearDown()
        throws Exception
    {
        this.test.touch( this.id + " tearDown()" );
    }

    public void assertValid()
        throws Exception
    {
        this.test.touch( this.id + " assertValid()" );
    }

    public void run()
        throws Exception
    {
        this.test.touch( this.id + " run()" );
    }
}
