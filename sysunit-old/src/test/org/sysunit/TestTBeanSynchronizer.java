package org.sysunit;

public class TestTBeanSynchronizer
    extends SysUnitTestCase {

    public void testConstruct() {
        Synchronizer synchronizer = new MockSynchronizer();

        TBeanSynchronizer tbeanSynchronizer = new TBeanSynchronizer( "one",
                                                                     synchronizer );

        assertEquals( "one",
                      tbeanSynchronizer.getTBeanId() );
        
        assertSame( synchronizer,
                    tbeanSynchronizer.getSynchronizer() );
    }

    public void testSync()
        throws Exception {

        MockSynchronizer synchronizer = new MockSynchronizer();

        TBeanSynchronizer tbeanSynchronizer = new TBeanSynchronizer( "one",
                                                                     synchronizer );

        tbeanSynchronizer.sync( "syncPoint.1" );

        assertEquals( "one",
                      synchronizer.getTBeanId() );

        assertEquals( "syncPoint.1",
                      synchronizer.getSyncPoint() );
    }
}
