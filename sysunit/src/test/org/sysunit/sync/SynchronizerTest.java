package org.sysunit.sync;

import org.sysunit.InconsistentSyncException;

public class SynchronizerTest
    extends SyncTestBase
    implements SynchronizerCallback
{
    private int touches;

    public void testValid()
        throws Exception
    {
        Synchronizer synchronizer = new Synchronizer( 2,
                                                      this );

        Thread thr1 = runThread( synchronizer, "one", false );

        Thread.sleep( 1000 );
        assertTouches( 0 );

        Thread thr2 = runThread( synchronizer, "one", false );

        thr1.join();
        thr2.join();

        assertTouches( 2 );
    }

    public void testInconsistent()
        throws Exception
    {
        Synchronizer synchronizer = new Synchronizer( 2,
                                                      this );

        Thread thr1 = runThread( synchronizer, "one", true );
        Thread thr2 = runThread( synchronizer, "two", true );

        thr1.join();
        thr2.join();

        assertTouches( 0 );
    }

    void assertTouches(int num)
    {
        if ( this.touches == num )
        {
            return;
        }

        fail( "expected <" + num + "> touches but found <" + this.touches + ">" );
    }

    Thread runThread(final Synchronizer synchronizer,
                     final String syncPoint,
                     final boolean expectInconsistent)
    {
        Thread thr = new Thread()
            {
                public void run()
                {
                    try
                    {
                        synchronizer.sync( syncPoint,
                                           getName() );

                        if ( expectInconsistent )
                        {
                            fail( "expected InconsistentSyncException" );
                        }
                        
                        touch();
                    }
                    catch (InconsistentSyncException e)
                    {
                        if ( ! expectInconsistent )
                        {
                            fail( e.getMessage() );
                        }
                    }
                    catch (Exception e)
                    {
                        fail( e.getMessage() );
                    }
                }
            };

        thr.start();

        return thr;
    }

    synchronized void touch()
    {
        ++this.touches;
    }

    public void notifyFullyBlocked(Synchronizer synchronizer)
    {
        synchronizer.unblock();
    }

    public void notifyInconsistent(Synchronizer synchronizer)
    {

    }
}
