package org.sysunit.testmesh.slavehost;

import org.sysunit.mesh.RemoteNodeInfo;
import org.sysunit.testmesh.PingPongNode;
import org.sysunit.testmesh.master.AddSlaveHostCommand;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class PingPongThread
    extends Thread
{
    private SlaveHostNode slaveHostNode;
    private InetAddress pingAddress;
    private DatagramSocket pingSocket;

    public PingPongThread(SlaveHostNode slaveHostNode,
                          InetAddress pingAddress)
    {
        super( "PingPongThread" );
        setDaemon( true );

        this.slaveHostNode = slaveHostNode;
        this.pingAddress   = pingAddress;
    }

    public SlaveHostNode getSlaveHostNode()
    {
        return this.slaveHostNode;
    }

    public int getPingPort()
    {
        return getSlaveHostNode().getPingPort();
    }

    public InetAddress getPingAddress()
    {
        return this.pingAddress;
    }

    public int getServerPort()
    {
        return getSlaveHostNode().getPort();
    }

    public void run()
    {
        this.pingSocket = null;

        try
        {
            if ( getPingAddress() != null )
            {
                try
                {
                    MulticastSocket mcastSocket = new MulticastSocket( getPingPort() );
                    mcastSocket.setSoTimeout( 100 );
                    mcastSocket.joinGroup( getPingAddress() );
                    pingSocket = mcastSocket;
                }
                catch (IOException e)
                {
                    // swallow, we'll retry below
                }
            } 
            
            if ( pingSocket == null )
            {
                try
                {
                    pingSocket = new DatagramSocket( getPingPort() + 1 );
                    pingSocket.setSoTimeout( 100 );
                }
                catch (SocketException e)
                {
                    //e.printStackTrace();
                    return;
                }
            }
            
            byte[] buf = new byte[256];
            
            DatagramPacket ping = new DatagramPacket( buf,
                                                      buf.length );
            
          LOOP:
            while ( true )
            {
                try
                {
                    pingSocket.receive( ping );
                    
                    String message = new String( ping.getData(),
                                                 0,
                                                 ping.getLength() );
                    
                    if ( message.startsWith( PingPongNode.PING_PREFIX ) )
                    {
                        String payload = message.substring( PingPongNode.PING_PREFIX.length() ).trim();
                        
                        try
                        {
                            InetAddress address = ping.getAddress();
                            int port = Integer.parseInt( payload );
                            
                            RemoteNodeInfo master = new RemoteNodeInfo( getSlaveHostNode(),
                                                                        "master",
                                                                        address,
                                                                        port );
                            
                            try
                            {
                                getSlaveHostNode().executeOn( master,
                                                              new AddSlaveHostCommand( getSlaveHostNode().getPhysicalMachineInfo() ) );
                            }
                            catch (Exception e)
                            {
                                // ignore, try again
                            }
                        }
                        catch (NumberFormatException e)
                        {
                            // ignore, try again
                        }
                    }
                }
                catch (SocketTimeoutException e)
                { 
                    continue LOOP;
                }
                catch (IOException e)
                {
                    //e.printStackTrace();
                    break LOOP;
                }
            }
        }
        finally
        {
            if ( this.pingSocket != null )
            {
                this.pingSocket.close();
            }
        }
    }
}
