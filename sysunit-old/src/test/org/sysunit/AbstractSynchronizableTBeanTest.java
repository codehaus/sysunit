package org.sysunit;

public class AbstractSynchronizableTBeanTest
    extends SysUnitTestCase
{
    public void testSynchronizer()
        throws Exception {

        AbstractSynchronizableTBean tbean = new AbstractSynchronizableTBean() {
                public void run()
                    throws Exception {
                    // nothing
                }
            };

        MockTBeanSynchronizer synchronizer = new MockTBeanSynchronizer();

        tbean.setSynchronizer( synchronizer );

        assertSame( synchronizer,
                    tbean.getSynchronizer() );

        tbean.sync( "one" );
        tbean.sync( "two" );
        tbean.sync( "three" );

        assertEquals( "one",
                      synchronizer.getSyncPoints()[0] );
        assertEquals( "two",
                      synchronizer.getSyncPoints()[1] );
        assertEquals( "three",
                      synchronizer.getSyncPoints()[2] );
    }
}
