package org.sysunit;

import java.util.Date;

public class TestWatchdog
    extends SysUnitTestCase
    implements Watched {

    private long triggered;

    public void testConstruct()
        throws Exception {

        long started = new Date().getTime();

        Watchdog watchdog = new Watchdog( this,
                                          3000 );

        Thread.sleep( 4000 );

        assertTrue( this.triggered != 0 );

        long diff = this.triggered - started;

        assertTrue( diff >= 2800 );
        assertTrue( diff <= 3200 );

        assertSame( this,
                    watchdog.getWatched() );

        assertEquals( 3000,
                      watchdog.getTimeout() );
    }

    public  void testConstruct_ZeroTimeout()
        throws Exception {
        Watchdog watchdog = new Watchdog( this,
                                          0 );

        Thread.sleep( 4000 );

        assertTrue( this.triggered == 0 );

        assertSame( this,
                    watchdog.getWatched() );

        assertEquals( 0,
                      watchdog.getTimeout() );
    }

    // ----------------------------------------------------------------------

    public void triggerTimeout() {
        this.triggered = new Date().getTime();
    }
        
}
