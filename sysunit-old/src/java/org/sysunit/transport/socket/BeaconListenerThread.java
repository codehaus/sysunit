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
            mcastSocket = mcastSocket;
        } catch (IOException e) {
            try {
                beaconSocket = new DatagramSocket( getBeaconPort() + 1 );
            } catch (SocketException ee) {
                log.error( ee );
                return;
            }
        }
        
        try {
            long startTime = System.currentTimeMillis();

            while ( true ) {
                if ( ( System.currentTimeMillis() - startTime ) >= getListenTime() ) {
                    break;
                }
                try {
                    beaconSocket.receive( packet );
                    handlePacket( packet );
                    Thread.sleep( 1000 );
                } catch (InterruptedException e) {
                    break;
                } catch (InterruptedIOException e) {
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

        log.debug( "received beacon [" + message + "] from " + packet.getAddress().getHostAddress() );

        if ( message.startsWith( "slave|" ) ) {
            String portStr = message.substring( message.indexOf( "|" )+1 );
            int port = Integer.parseInt( portStr );

            SlaveNodeInfo slaveNode = new SlaveNodeInfo( packet.getAddress(),
                                                         port );

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
