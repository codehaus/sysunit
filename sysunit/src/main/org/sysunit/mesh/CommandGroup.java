package org.sysunit.mesh;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class CommandGroup
{
    private List uids;

    private Map inFlightCommands;

    CommandGroup(Map inFlightCommands)
    {
        this.inFlightCommands = inFlightCommands;
        this.uids             = new ArrayList();
    }

    public void add(int uid)
    {
        this.uids.add( uid + "" );
    }

    public void waitFor()
        throws InterruptedException
    {
        synchronized ( this.inFlightCommands )
        {
          WAIT:
            while ( true )
            {
                List notInFlight = new ArrayList( this.uids );

                notInFlight.removeAll( this.inFlightCommands.keySet() );

                if ( notInFlight.size() == this.uids.size() )
                {
                    break WAIT;
                }

                this.inFlightCommands.wait();
            }
        }
    }
}
