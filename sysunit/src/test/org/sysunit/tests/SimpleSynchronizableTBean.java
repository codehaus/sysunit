package org.sysunit.tests;

import org.sysunit.SynchronizableTBean;
import org.sysunit.TBeanSynchronizer;

public class SimpleSynchronizableTBean
    implements SynchronizableTBean
{
    private TBeanSynchronizer sync;

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
        this.sync.sync( "one" );
        this.sync.sync( "two" );
        this.sync.sync( "three" );
        this.sync.sync( "four" );
    }

    public void setSynchronizer(TBeanSynchronizer sync)
    {
        this.sync = sync;
    }
}
