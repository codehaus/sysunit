package org.sysunit.local;

import org.sysunit.TBean;
import org.sysunit.NoOpTBean;
import org.sysunit.NoOpSynchronizableTBean;
import org.sysunit.TBeanSynchronizer;
import org.sysunit.SysUnitTestCase;

public class TestTBeanThread
    extends SysUnitTestCase {

    public void testConstruct() {

        TBean tbean = new NoOpTBean();

        LocalSynchronizer synchronizer = new LocalSynchronizer();

        Barrier barrier = new Barrier( 1 );

        TBeanThread thread = new TBeanThread( "one",
                                              tbean,
                                              synchronizer,
                                              barrier );

        assertEquals( "one",
                      thread.getTBeanId() );

        assertSame( tbean,
                    thread.getTBean() );

        assertSame( synchronizer,
                    thread.getSynchronizer() );

        assertSame( barrier,
                    thread.getBarrier() );
    }

    public void testSetUpSynchronizer() {
        NoOpSynchronizableTBean tbean = new NoOpSynchronizableTBean();

        LocalSynchronizer synchronizer = new LocalSynchronizer();

        Barrier barrier = new Barrier( 1 );

        TBeanThread thread = new TBeanThread( "one",
                                              tbean,
                                              synchronizer,
                                              barrier );

        assertEquals( "one",
                      thread.getTBeanId() );

        assertSame( tbean,
                    thread.getTBean() );

        assertSame( synchronizer,
                    thread.getSynchronizer() );

        assertSame( barrier,
                    thread.getBarrier() );

        TBeanSynchronizer tbeanSynchronizer = tbean.getSynchronizer();

        assertNull( tbeanSynchronizer );

        thread.setUpSynchronizer();

        tbeanSynchronizer = tbean.getSynchronizer();

        assertNotNull( tbeanSynchronizer );
    }
}
