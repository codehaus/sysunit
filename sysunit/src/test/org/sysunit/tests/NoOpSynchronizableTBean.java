package org.sysunit.tests;

import org.sysunit.SynchronizableTBean;
import org.sysunit.TBeanSynchronizer;

public class NoOpSynchronizableTBean
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

    }

    public void setSynchronizer(TBeanSynchronizer sync)
    {
        this.sync = sync;
    }
}
