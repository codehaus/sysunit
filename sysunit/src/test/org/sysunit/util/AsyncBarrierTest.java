package org.sysunit.util;

public class AsyncBarrierTest
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
        AsyncBarrier barrier = new AsyncBarrier( 1 );

        assertEquals( "barrier.getNumThreads()",
                      1,
                      barrier.getNumThreads() );

        runThread( barrier );

        assertEquals( "barrier.getNumWaitingThreads()",
                      0,
                      barrier.getNumWaitingThreads() );

        assertTouches( 1 );
    }

    public void testFiveThreads()
        throws Exception
    {
        AsyncBarrier barrier = new AsyncBarrier( 5 );

        assertEquals( "barrier.getNumThreads()",
                      5,
                      barrier.getNumThreads() );

        runThread( barrier );

        assertEquals( "barrier.getNumWaitingThreads()",
                      1,
                      barrier.getNumWaitingThreads() );

        runThread( barrier );

        assertEquals( "barrier.getNumWaitingThreads()",
                      2,
                      barrier.getNumWaitingThreads() );

        runThread( barrier );

        assertEquals( "barrier.getNumWaitingThreads()",
                      3,
                      barrier.getNumWaitingThreads() );

        runThread( barrier );

        assertEquals( "barrier.getNumWaitingThreads()",
                      4,
                      barrier.getNumWaitingThreads() );

        runThread( barrier );

        assertEquals( "barrier.getNumWaitingThreads()",
                      0,
                      barrier.getNumWaitingThreads() );


        assertTouches( 5 );
    }

    void runThread(AsyncBarrier barrier)
    {
        barrier.block( new AsyncBarrierCallback()
            {
                public void unblock(AsyncBarrier barrier)
                {
                    touch();
                }
            } );
    }

    void touch()
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
