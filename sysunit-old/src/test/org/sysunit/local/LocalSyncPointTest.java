package org.sysunit.local;

import org.sysunit.SysUnitTestCase;

public class LocalSyncPointTest
    extends SysUnitTestCase {

    public void testConstruct() {
        LocalSyncPoint syncPoint = new LocalSyncPoint( "cheese" );

        assertEquals( "cheese",
                      syncPoint.getName() );
    }

    public void testSync_OneThread()
        throws Exception {

        final LocalSyncPoint syncPoint = new LocalSyncPoint( "cheese" );

        final StringBuffer buf = new StringBuffer();

        Thread threadOne = new Thread() {
                public void run() {
                    buf.append( "before.sync" );
                    try {
                        syncPoint.sync( "one" );
                    } catch (Exception e) {
                        fail( e.getMessage() );
                    }
                    buf.append( "after.sync" );
                }
            };

        threadOne.start();

        Thread.sleep( 500 );

        assertEquals( "before.sync",
                      buf.toString() );

        Thread.sleep( 500 );

        assertEquals( "before.sync",
                      buf.toString() );

        syncPoint.unblockAll();

        threadOne.join();

        assertEquals( "before.syncafter.sync",
                      buf.toString() );
    }

    public void testSync_OneThread_SpuriousWakeup()
        throws Exception {

        final LocalSyncPoint syncPoint = new LocalSyncPoint( "cheese" );

        final StringBuffer buf = new StringBuffer();

        Thread threadOne = new Thread() {
                public void run() {
                    buf.append( "before.sync" );
                    try {
                        syncPoint.sync( "one" );
                    } catch (Exception e) {
                        fail( e.getMessage() );
                    }
                    buf.append( "after.sync" );
                }
            };

        threadOne.start();

        Thread.sleep( 500 );

        assertEquals( "before.sync",
                      buf.toString() );

        Thread.sleep( 500 );

        assertEquals( "before.sync",
                      buf.toString() );

        synchronized ( syncPoint ) {
            syncPoint.notifyAll();
        }

        Thread.sleep( 500 );

        synchronized ( syncPoint ) {
            syncPoint.notifyAll();
        }

        Thread.sleep( 500 );

        assertEquals( "before.sync",
                      buf.toString() );

        syncPoint.unblockAll();

        threadOne.join();

        assertEquals( "before.syncafter.sync",
                      buf.toString() );
    }
}
