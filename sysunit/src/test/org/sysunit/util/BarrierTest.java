package org.sysunit.util;

public class BarrierTest
    extends UtilTestBase
{
    private int touches;

    public void setUp()
    {
        this.touches = 0;
    }

    public void tearDown()
    {
        this.touches = 0;
    }

    public void testOneThread()
        throws Exception
    {
        Barrier barrier = new Barrier( 1 );

        assertEquals( 0,
                      barrier.getNumWaitingThreads() );

        assertEquals( 1,
                      barrier.getNumThreads() );

        runThread( barrier );

        Thread.currentThread().sleep( 1000 );

        assertTouches( 1 );
    }

    public void testFiveThreads()
        throws Exception
    {
        Barrier barrier = new Barrier( 5 );

        assertEquals( 0,
                      barrier.getNumWaitingThreads() );

        assertEquals( 5,
                      barrier.getNumThreads() );

        runThread( barrier );
        runThread( barrier );
        runThread( barrier );
        runThread( barrier );
        runThread( barrier );

        Thread.currentThread().sleep( 1000 );

        assertTouches( 5 );
    }

    public void testBarrierReuse()
        throws Exception
    {
        Barrier barrier = new Barrier( 5 );

        runThread( barrier );
        runThread( barrier );
        runThread( barrier );
        runThread( barrier );
        runThread( barrier );

        Thread.currentThread().sleep( 1000 );

        assertTouches( 5 );

        runThread( barrier );
        runThread( barrier );
        runThread( barrier );
        runThread( barrier );
        runThread( barrier );

        Thread.currentThread().sleep( 1000 );

        assertTouches( 10 );
    }

    public void testBarrierStepWise()
        throws Exception
    {
        Barrier barrier = new Barrier( 5 );

        runThread( barrier );

        Thread.currentThread().sleep( 500 );
        assertTouches( 0 );
        assertEquals( 1,
                      barrier.getNumWaitingThreads() );

        runThread( barrier );
        Thread.currentThread().sleep( 500 );
        assertTouches( 0 );
        assertEquals( 2,
                      barrier.getNumWaitingThreads() );

        runThread( barrier );
        Thread.currentThread().sleep( 500 );
        assertTouches( 0 );
        assertEquals( 3,
                      barrier.getNumWaitingThreads() );

        runThread( barrier );
        Thread.currentThread().sleep( 500 );
        assertTouches( 0 );
        assertEquals( 4,
                      barrier.getNumWaitingThreads() );

        runThread( barrier );
        Thread.currentThread().sleep( 500 );
        assertTouches( 5 );
        assertEquals( 0,
                      barrier.getNumWaitingThreads() );
    }

    void runThread(final Barrier barrier)
    {
        new Thread()
        {
            public void run()
            {
                try
                {
                    barrier.block();
                }
                catch (InterruptedException e)
                {
                    fail( "caught InterruptedException" );
                }
                
                touch();
            }
        }.start();
    }

    synchronized void touch()
    {
        ++this.touches;
    }

    void assertTouches(int num)
    {
        if ( num == this.touches )
        {
            return;
        }

        fail( "expected <" + num + "> touches, but found <" + this.touches + ">" );
    }
}
