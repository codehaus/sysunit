package org.sysunit.mesh;

import org.sysunit.net.Server;
import org.sysunit.net.ServerLoop;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.InetAddress;

public class NetworkedNode
    extends Node
    implements ServerLoop
{
    private Server server;

    public NetworkedNode(String name)
    {
        super( name );

        this.server = new Server( 1,
                                  this );
    }

    public void start()
        throws Exception
    {
        super.start();
        this.server.start();
    }

    public void stop()
        throws InterruptedException
    {
        //System.err.println( "NetworkedNode::stop()" );
        super.stop();
        this.server.stop();
        //System.err.println( "NetworkedNode::complete()" );
    }

    protected Server getServer()
    {
        return this.server;
    }

    public int getPort()
    {
        return getServer().getPort();
    }
    
    public void accept(InetAddress remoteAddress,
                       int remotePort,
                       InputStream in,
                       OutputStream out)
        throws Exception
    {
        ObjectInputStream objectIn = new ObjectInputStream( in );
        
        String  originName = (String) objectIn.readObject();
        Integer originPort = (Integer) objectIn.readObject();

        NodeInfo origin = new RemoteNodeInfo( this,
                                              originName,
                                              remoteAddress,
                                              originPort.intValue() );
        
        Command command = (Command) objectIn.readObject();
            
        execute( origin,
                 command );
    }
}
