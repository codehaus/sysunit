package org.sysunit.tests;

import org.sysunit.SystemTestCase;
import org.sysunit.TBean;

import java.util.List;
import java.util.ArrayList;

public class WatchedSystemTestCase
    extends SystemTestCase
{
    private List touches;

    public WatchedSystemTestCase()
    {
        this.touches = new ArrayList();
    }

    public String[] getTouches()
    {
        return (String[]) this.touches.toArray( new String[ this.touches.size() ] );
    }

    protected synchronized void touch(String id)
    {
        this.touches.add( id );
    }

    TBean newTBean(String id)
    {
        return new WatchedTBean( this,
                                 id );
    }

    TBean newSyncTBean(String id,
                       String[] syncPoints)
    {
        return new WatchedSynchronizableTBean( this,
                                               id,
                                               syncPoints );
    }
}
