package org.sysunit.local;

import org.sysunit.SysUnitTestCase;

public class BarrierTest
    extends SysUnitTestCase
{
    public void testConstruct() {
        Barrier barrier = new Barrier( 3 );

        assertEquals( 3,
                      barrier.getNumThreads() );
    }

    public void testBlock()
        throws Exception {

        final Barrier barrier = new Barrier( 3 );

        final StringBuffer bufOne = new StringBuffer();

        Thread threadOne = new Thread() {
                public void run() {
                    try {
                        bufOne.append( "pre" );
                        barrier.block();
                        bufOne.append( "post" );
                    } catch (Exception e) {
                        fail( e.getMessage() );
                    }
                }
            };

        threadOne.start();

        Thread.sleep( 500 );

        assertEquals( "pre",
                      bufOne.toString() );

        final StringBuffer bufTwo = new StringBuffer();

        Thread threadTwo = new Thread() {
                public void run() {
                    try {
                        bufTwo.append( "pre" );
                        barrier.block();
                        bufTwo.append( "post" );
                    } catch (Exception e) {
                        fail( e.getMessage() );
                    }
                }
            };

        threadTwo.start();

        Thread.sleep( 500 );

        assertEquals( "pre",
                      bufOne.toString() );

        assertEquals( "pre",
                      bufTwo.toString() );

        final StringBuffer bufThree = new StringBuffer();

        Thread threadThree = new Thread() {
                public void run() {
                    try {
                        bufThree.append( "pre" );
                        barrier.block();
                        bufThree.append( "post" );
                    } catch (Exception e) {
                        fail( e.getMessage() );
                    }
                }
            };

        threadThree.start();

        Thread.sleep( 500 );

        assertEquals( "prepost",
                      bufOne.toString() );

        assertEquals( "prepost",
                      bufTwo.toString() );

        assertEquals( "prepost",
                      bufThree.toString() );
    }
}
