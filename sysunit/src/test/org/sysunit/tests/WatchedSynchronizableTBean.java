package org.sysunit.tests;

import org.sysunit.SynchronizableTBean;
import org.sysunit.TBeanSynchronizer;

public class WatchedSynchronizableTBean
    implements SynchronizableTBean
{
    private WatchedSystemTestCase test;
    private String id;
    private String[] syncPoints;

    private TBeanSynchronizer sync;

    public WatchedSynchronizableTBean(WatchedSystemTestCase test,
                                      String id,
                                      String[] syncPoints)
    {
        this.test = test;
        this.id = id;
        this.syncPoints = syncPoints;
    }

    public void setSynchronizer(TBeanSynchronizer sync)
    {
        this.sync = sync;
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
        this.test.touch( this.id + " start run()" );
        for ( int i = 0 ; i < this.syncPoints.length ; ++i )
        {
            this.test.touch( this.id + " before sync(" + this.syncPoints[i] + ")" );
            this.sync.sync( this.syncPoints[i] );
            this.test.touch( this.id + " after sync(" + this.syncPoints[i] + ")" );
        }
        this.test.touch( this.id + " end run()" );
    }
}
