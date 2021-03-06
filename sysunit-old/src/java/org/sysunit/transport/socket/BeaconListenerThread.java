package org.sysunit.transport.socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Set;
import java.util.HashSet;

public class BeaconListenerThread
    extends Thread {

    private static final Log log = LogFactory.getLog( BeaconListenerThread.class );

    private InetAddress beaconAddr;
    private int beaconPort;
    private long listenTime;

    private Set slaveNodes;

    public BeaconListenerThread(InetAddress beaconAddr,
                                int beaconPort,
                                long listenTime) {
        this.beaconAddr = beaconAddr;
        this.beaconPort = beaconPort;
        this.listenTime = listenTime;
        this.slaveNodes = new HashSet();
    }

    public InetAddress getBeaconAddr() {
        return this.beaconAddr;
    }

    public int getBeaconPort() {
        return this.beaconPort;
    }

    public long getListenTime() {
        return this.listenTime;
    }

    public void run() {

        DatagramSocket beaconSocket = null;

        byte[] buf = new byte[256];
        
        DatagramPacket packet = new DatagramPacket( buf,
                                                    buf.length );

        try {
            MulticastSocket mcastSocket = new MulticastSocket( getBeaconPort() );
            mcastSocket.setSoTimeout( 1000 );
            mcastSocket.joinGroup( getBeaconAddr() );
            beaconSocket = mcastSocket;
            log.debug( "mcast socketing" );
        } catch (IOException e) {
            try {
                beaconSocket = new DatagramSocket( getBeaconPort() + 1 );
                log.debug( "dgram socketing" );
            } catch (SocketException ee) {
                log.error( ee );
                return;
            }
        }
        
        try {
            long startTime = System.currentTimeMillis();

            while ( true ) {
                if ( ( System.currentTimeMillis() - startTime ) >= getListenTime() ) {
                    log.debug( "time exceeded" );
                    break;
                }
                try {
                    beaconSocket.receive( packet );
                    handlePacket( packet );
                    Thread.sleep( 1000 );
                } catch (InterruptedException e) {
                    break;
                } catch (InterruptedIOException e) {
                    log.debug( "timeout on receive" );
                    // swallow
                }
            }
        } catch (Exception  e) {
            log.error( e );
        }

        log.debug( "exiting listener" );
    }

    public void handlePacket(DatagramPacket packet)
        throws Exception {

        String message = new String( packet.getData(),
                                     0,
                                     packet.getLength() );

        //System.err.println( "received beacon [" + message + "] from " + packet.getAddress().getHostAddress() );

        if ( message.startsWith( "slave|" ) ) {
            int portIndex = message.indexOf( "|" ) + 1;
            int typeIndex = message.indexOf( "|", portIndex ) + 1;
            
            String portStr = message.substring( portIndex,
                                             typeIndex - 2 );
            
            int port = Integer.parseInt( portStr );

            String type = message.substring( typeIndex );
            
            SlaveNodeInfo slaveNode = new SlaveNodeInfo( packet.getAddress(),
                                                         port,
                                                         type );

            if ( ! this.slaveNodes.contains( slaveNode ) ) {
                log.debug( "adding slave node: " + slaveNode );
                this.slaveNodes.add( slaveNode );
            }
        }
    }

    public SlaveNodeInfo[] getSlaveNodes() {
        return (SlaveNodeInfo[]) this.slaveNodes.toArray( SlaveNodeInfo.EMPTY_ARRAY );
    }
}
