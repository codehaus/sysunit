package org.sysunit.local;

import org.sysunit.SysUnitTestCase;
import org.sysunit.AlreadySynchronizedException;

public class TestLocalSynchronizer
    extends SysUnitTestCase {

    public void testConstruct() {
        LocalSynchronizer synchronizer = new LocalSynchronizer();

        assertLength( 0,
                      synchronizer.getRegisteredTBeans() );

        assertLength( 0,
                      synchronizer.getWaitingTBeans() );

        assertLength( 0,
                      synchronizer.getSyncPoints() );
    }

    public void testRegistration_Simple() {
        LocalSynchronizer synchronizer = new LocalSynchronizer();

        synchronizer.registerSynchronizableTBean( "one" );
        synchronizer.registerSynchronizableTBean( "one" );

        assertLength( 1,
                      synchronizer.getRegisteredTBeans() );

        assertContainsEquals( "one",
                              synchronizer.getRegisteredTBeans() );

        synchronizer.registerSynchronizableTBean( "two" );
        synchronizer.registerSynchronizableTBean( "two" );

        assertLength( 2,
                      synchronizer.getRegisteredTBeans() );

        assertContainsEquals( "one",
                              synchronizer.getRegisteredTBeans() );

        assertContainsEquals( "two",
                              synchronizer.getRegisteredTBeans() );

        synchronizer.unregisterSynchronizableTBean( "one" );
        synchronizer.unregisterSynchronizableTBean( "one" );

        assertLength( 1,
                      synchronizer.getRegisteredTBeans() );

        assertContainsEquals( "two",
                              synchronizer.getRegisteredTBeans() );

        synchronizer.unregisterSynchronizableTBean( "two" );
        synchronizer.unregisterSynchronizableTBean( "two" );

        assertLength( 0,
                      synchronizer.getRegisteredTBeans() );
    }

    public void testSync_OneTBean()
        throws Exception {
        final LocalSynchronizer synchronizer = new LocalSynchronizer();

        synchronizer.registerSynchronizableTBean( "one" );

        final StringBuffer buf = new StringBuffer();

        Thread threadOne = new Thread() {
                public void run() {
                    try {
                        synchronizer.sync( "one",
                                           "syncpoint.1" );
                    } catch (Exception e) {
                        fail( e.getMessage() );
                    } 
                        
                    buf.append( "cheese" );
                }
            };

        threadOne.start();

        Thread.sleep( 500 );

        assertEquals( "cheese",
                      buf.toString() );
        
        threadOne.join();

    }

    public void testSync_TwoTBeans()
        throws Exception {

        final LocalSynchronizer synchronizer = new LocalSynchronizer();

        synchronizer.registerSynchronizableTBean( "one" );
        synchronizer.registerSynchronizableTBean( "two" );

        final StringBuffer bufOne = new StringBuffer();

        Thread threadOne = new Thread() {
                public void run() {
                    try {
                        synchronizer.sync( "one",
                                           "syncpoint.1" );
                    } catch (Exception e) {
                        fail( e.getMessage() );
                    } 
                        
                    bufOne.append( "cheeseOne" );
                }
            };

        final StringBuffer bufTwo = new StringBuffer();

        Thread threadTwo = new Thread() {
                public void run() {
                    try {
                        synchronizer.sync( "two",
                                           "syncpoint.1" );
                    } catch (Exception e) {
                        fail( e.getMessage() );
                    } 
                        
                    bufTwo.append( "cheeseTwo" );
                }
            };

        threadOne.start();

        Thread.sleep( 1000 );

        assertEquals( "",
                      bufOne.toString() );

        assertEquals( "",
                      bufTwo.toString() );

        threadTwo.start();

        Thread.sleep( 1000 );
        
        assertEquals( "cheeseOne",
                      bufOne.toString() );

        assertEquals( "cheeseTwo",
                      bufTwo.toString() );

        threadOne.join();
        threadTwo.join();

    }

    public void testSync_TwoTBeans_OneExits()
        throws Exception {

        final LocalSynchronizer synchronizer = new LocalSynchronizer();

        synchronizer.registerSynchronizableTBean( "one" );
        synchronizer.registerSynchronizableTBean( "two" );

        final StringBuffer bufOne = new StringBuffer();

        Thread threadOne = new Thread() {
                public void run() {
                    try {
                        synchronizer.sync( "one",
                                           "syncpoint.1" );
                    } catch (Exception e) {
                        fail( e.getMessage() );
                    } 
                        
                    bufOne.append( "cheeseOne" );
                }
            };

        threadOne.start();

        Thread.sleep( 1000 );

        assertEquals( "",
                      bufOne.toString() );

        synchronizer.unregisterSynchronizableTBean( "two" );
        
        threadOne.join();

        assertEquals( "cheeseOne",
                      bufOne.toString() );
    }

    /*
    public void testSync_AlreadySynchronized()
        throws Exception {

        final LocalSynchronizer synchronizer = new LocalSynchronizer();

        synchronizer.registerSynchronizableTBean( "one" );
        synchronizer.registerSynchronizableTBean( "two" );

        Thread threadOne = new Thread() {
                public void run() {
                    try {
                        synchronizer.sync( "one",
                                           "syncpoint.1" );
                    } catch (Exception e) {
                        fail( e.getMessage() );
                    } 
                }
            };
        
        threadOne.setDaemon( true );

        Thread watchdog = new Thread() {
                public void run() {
                    //System.exit( 1 );
                    fail( "watchdoggedly" );
                }
            };

        watchdog.setDaemon( true );

        // threadOne.start();
        watchdog.start();

        try {
            synchronizer.sync( "one",
                               "syncpoint.2" );
            fail( "should have thrown AlreadySynchronizedException" );
        } catch (AlreadySynchronizedException e) {
            // expected and correct

            assertEquals( "one",
                          e.getTBeanId() );

            assertEquals( "syncpoint.2",
                          e.getSyncPoint() );
        }

        // threadOne.join();
    }
    */
}
