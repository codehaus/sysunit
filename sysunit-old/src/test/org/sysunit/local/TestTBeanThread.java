package org.sysunit.local;

import org.sysunit.TBean;
import org.sysunit.FailTBean;
import org.sysunit.NoOpTBean;
import org.sysunit.AbstractTBean;
import org.sysunit.NoOpSynchronizableTBean;
import org.sysunit.TBeanSynchronizer;
import org.sysunit.SysUnitTestCase;

import junit.framework.AssertionFailedError;

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

    public void testErrors() {
        TBeanThread thread = new TBeanThread( "one",
                                              null,
                                              null,
                                              null );

        assertFalse( thread.hasError() );
        assertNull( thread.getError() );

        Throwable error = new Throwable();

        thread.setError( error );

        assertTrue( thread.hasError() );

        assertSame( error,
                    thread.getError() );
    }

    public void testRun_Okay() {
        NoOpTBean tbean = new NoOpTBean();

        LocalSynchronizer synchronizer = new LocalSynchronizer();

        Barrier barrier = new Barrier( 1 );

        TBeanThread thread = new TBeanThread( "one",
                                              tbean,
                                              synchronizer,
                                              barrier );

        thread.run();

        assertFalse( thread.hasError() );
    }

    public void testRun_WithFailure() {
        FailTBean tbean = new FailTBean();

        LocalSynchronizer synchronizer = new LocalSynchronizer();

        Barrier barrier = new Barrier( 1 );

        TBeanThread thread = new TBeanThread( "one",
                                              tbean,
                                              synchronizer,
                                              barrier );

        thread.run();

        assertTrue( thread.hasError() );

        assertTrue( thread.getError() instanceof AssertionFailedError );
    }

    public void testRun_WithError() {

        final NullPointerException e = new NullPointerException();

        TBean tbean = new AbstractTBean() {
                public void run()
                    throws Throwable {
                    throw e;
                }
            };

        LocalSynchronizer synchronizer = new LocalSynchronizer();

        Barrier barrier = new Barrier( 1 );

        TBeanThread thread = new TBeanThread( "one",
                                              tbean,
                                              synchronizer,
                                              barrier );

        thread.run();

        assertTrue( thread.hasError() );

        assertSame( e,
                    thread.getError() );
    }
}
