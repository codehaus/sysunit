package org.sysunit.testmesh;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.sysunit.mesh.NetworkedNode;

public class PingPongNode
    extends NetworkedNode
{
    public static final String PING_PREFIX = "sysunit.ping.";

    private boolean multicast;

    public PingPongNode(String name)
    {
        super( name );
        this.multicast = true;
    }

    public void disableMulticast()
    {
        this.multicast = false;
    }

    public boolean isMulticastEnabled()
    {
        return this.multicast;
    }

    public InetAddress getPingAddress()
    {
        if ( ! this.multicast )
        {
            return null;
        }

        try
        {
            return InetAddress.getByName( "224.0.0.42" );
        }
        catch (UnknownHostException e)
        {
            return null;
        }
    }

    public int getPingPort()
    {
        return 4242;
    }
}
