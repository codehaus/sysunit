package org.sysunit.mesh;

import java.io.Serializable;
import java.io.OutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class LocalNodeInfo
    implements NodeInfo, Serializable
{
    private Node node;

    public LocalNodeInfo(Node thisNode,
                         Node node)
    {
        this.node     = node;
    }

    public Node getNode()
    {
        return this.node;
    }

    public String getName()
    {
        return getNode().getName();
    }

    void execute(LocalNodeInfo origin,
                 Command command)
        throws Exception
    {
        getNode().execute( origin,
                           command );
    }

    public void reportError(int reportingUid,
                            Throwable thrown)
        throws Exception
    {
        getNode().reportError( reportingUid,
                               thrown );
    }

    public void reportCompletion(int reportingUid)
        throws Exception
    {
        getNode().reportCompletion( reportingUid );
    }

    public boolean equals(Object thatObj)
    {
        LocalNodeInfo that = (LocalNodeInfo) thatObj;

        return ( this.node.equals( that.node ) );
    }

    public int hashCode()
    {
        return this.node.hashCode();
    }

    public String toString()
    {
        return "[LocalNodeInfo: node=" + this.node + "]";
    }
    
}
