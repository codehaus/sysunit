package org.sysunit.transport.socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class BeaconTransmitterThread
    extends Thread {

    private static final Log log = LogFactory.getLog( BeaconTransmitterThread.class );

    public static final int PORT = 30808;

    private InetAddress beaconAddr;
    private int beaconPort;

    private InetAddress commandAddr;
    private int commandPort;

    public BeaconTransmitterThread(InetAddress beaconAddr,
                                   int beaconPort,
                                   InetAddress commandAddr,
                                   int commandPort) {
        this.beaconAddr = beaconAddr;
        this.beaconPort = beaconPort;

        this.commandAddr = commandAddr;
        this.commandPort = commandPort;
    }

    public InetAddress getBeaconAddr() {
        return this.beaconAddr;
    }

    public int getBeaconPort() {
        return this.beaconPort;
    }

    public InetAddress getCommandAddr() {
        return this.commandAddr;
    }

    public int getCommandPort() {
        return this.commandPort;
    }

    public void run() {

        DatagramSocket beaconSocket = null;
        DatagramPacket beacon       = null;

        String message = "slave|" + getCommandPort();
        
        byte[] messageBytes = message.getBytes();
        
        try {

            MulticastSocket mcastSocket = new MulticastSocket( getBeaconPort() );
            mcastSocket.joinGroup( getBeaconAddr() );
            beaconSocket = mcastSocket;

            beacon = new DatagramPacket( messageBytes,
                                         messageBytes.length,
                                         getBeaconAddr(),
                                         getBeaconPort() );

        } catch (IOException e) {
            try {
                beaconSocket = new DatagramSocket(); 
                
                beacon = new DatagramPacket( messageBytes,
                                             messageBytes.length,
                                             InetAddress.getLocalHost(),
                                             getBeaconPort() + 1 );
            } catch (SocketException ee) {
                log.error( ee );
                return;
            } catch (UnknownHostException ee) {
                log.error( ee );
                return;
            }
        }
        
        try
        {
            while ( true ) {
                log.debug( "sending beacon [" + message + "]" );
                beaconSocket.send( beacon );
                try {
                    Thread.sleep( 1000 );
                } catch (InterruptedException e) {
                    break;
                }
            }
        } catch (IOException e) {
            log.error( e );
        }
    }
}
