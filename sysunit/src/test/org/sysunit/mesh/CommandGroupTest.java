package org.sysunit.mesh;

import java.util.Map;
import java.util.HashMap;

public class CommandGroupTest
    extends MeshTestBase
{
    private boolean touched;

    public void setUp()
    {
        this.touched = false;
    }

    public void testNoneInFlight()
        throws Exception
    {
        Map map = new HashMap();

        CommandGroup group = new CommandGroup( map );

        group.add( 1 );
        group.add( 2 );
        group.add( 3 );

        waitFor( group );

        Thread.sleep( 1000 );

        assertTouched();
    }

    public void testInFlight()
        throws Exception
    {
        Map map = new HashMap();

        CommandGroup group = new CommandGroup( map );

        group.add( 1 );
        group.add( 2 );
        group.add( 3 );

        map.put( "1",
                 Boolean.TRUE );
        map.put( "2",
                 Boolean.TRUE );
        map.put( "3",
                 Boolean.TRUE );
        map.put( "4",
                 Boolean.TRUE );

        waitFor( group );

        Thread.sleep( 1000 );

        assertNotTouched();

        synchronized ( map )
        {
            map.remove( "1" );
            map.notifyAll();
        }

        Thread.sleep( 200 );
        assertNotTouched();

        synchronized ( map )
        {
            map.remove( "2" );
            map.notifyAll();
        }

        Thread.sleep( 200 );
        assertNotTouched();

        synchronized ( map )
        {
            map.remove( "4" );
            map.notifyAll();
        }

        Thread.sleep( 200 );
        assertNotTouched();

        synchronized ( map )
        {
            map.remove( "3" );
            map.notifyAll();
        }

        Thread.sleep( 200 );
        assertTouched();
    }

    void waitFor(final CommandGroup group)
    {
        Thread thr = new Thread()
            {
                public void run()
                {
                    try
                    {
                        group.waitFor();
                        touch();
                    }
                    catch (InterruptedException e)
                    {
                        fail( e.getMessage() );
                    }
                }
            };

        thr.start();
    }

    void touch()
    {
        this.touched = true;
    }

    void assertTouched()
    {
        if ( ! this.touched )
        {
            fail( "expected to be touched" );
        }
    }

    void assertNotTouched()
    {
        if ( this.touched )
        {
            fail( "expected to be not touched" );
        }
    }
}
