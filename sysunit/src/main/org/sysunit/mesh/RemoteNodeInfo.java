package org.sysunit.mesh;

import java.io.Serializable;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class RemoteNodeInfo
    implements NodeInfo, Serializable
{
    private NetworkedNode thisNode;

    private String name;
    private InetAddress address;
    private int port;

    public RemoteNodeInfo(NetworkedNode thisNode,
                          String name,
                          InetAddress address,
                          int port)
    {
        this.thisNode = thisNode;

        this.name     = name;
        this.address  = address;
        this.port     = port;
    }

    public NetworkedNode getThisNode()
    {
        return this.thisNode;
    }

    public String getName()
    {
        return this.name;
    }

    public InetAddress getAddress()
    {
        return this.address;
    }

    public int getPort()
    {
        return this.port;
    }

    void execute(Command command)
        throws Exception
    {
        Socket socket = new Socket( getAddress(),
                                    getPort() );

        OutputStream       socketOut = socket.getOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream( socketOut );

        try
        {
            objectOut.writeObject( getThisNode().getName() );
            objectOut.writeObject( new Integer( getThisNode().getPort() ) );
            objectOut.writeObject( command );
        }
        finally
        {
            objectOut.close();
        }
    }

    public void reportException(int reportingUid,
                                Throwable thrown)
        throws Exception
    {
        execute( new ReportExceptionCommand( reportingUid,
                                             thrown ) );
    }

    public void reportCompletion(int reportingUid)
        throws Exception
    {
        execute( new ReportCompletionCommand( reportingUid ) );
    }

    public boolean equals(Object thatObj)
    {
        RemoteNodeInfo that = (RemoteNodeInfo) thatObj;

        return ( this.address.equals( that.address )
                 &&
                 ( this.port == that.port ) );
    }

    public int hashCode()
    {
        return this.address.hashCode() + this.port;
    }

    public String toString()
    {
        return "[RemoteNodeInfo: name=" + this.name + "; address=" + this.address + "; port=" + this.port + "]";
    }
}
