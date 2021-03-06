package org.sysunit.transport.socket;

import org.sysunit.command.slave.SlaveServer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetAddress;

public class SlaveNode
    extends Node {

    private static final Log log = LogFactory.getLog( SlaveNode.class );

    public static void main(String[] args)
        throws Exception {

        int port = BeaconTransmitterThread.PORT;

        String sysunitPortStr = System.getProperty( "sysunit.port" );

        if ( sysunitPortStr != null ) {
            sysunitPortStr = sysunitPortStr.trim();

            if ( ! sysunitPortStr.equals( "" ) ) {
                port = Integer.parseInt( sysunitPortStr );
            }
        }

        SlaveNode node = new SlaveNode( InetAddress.getByName( "224.0.0.42" ),
                                        port );
        node.start();
    }

    private InetAddress beaconAddr;
    private int beaconPort;

    public SlaveNode(InetAddress beaconAddr,
                     int beaconPort) {

        this.beaconAddr = beaconAddr;
        this.beaconPort = beaconPort;
    }

    public InetAddress getBeaconAddr() {
        return this.beaconAddr;
    }

    public int getBeaconPort() {
        return this.beaconPort;
    }

    public void start()
        throws Exception {
        System.err.println( "STARTING SLAVE" );
        log.debug( "start()" );

        SlaveServer server = new SlaveServer();

        CommandThread commandThread = createCommandThread( server );

        BeaconTransmitterThread beaconThread = new BeaconTransmitterThread( getBeaconAddr(),
                                                                            getBeaconPort(),
                                                                            commandThread.getServerSocket().getInetAddress(),
                                                                            commandThread.getServerSocket().getLocalPort() );

        beaconThread.setDaemon( false );
        beaconThread.start();

        server.start();
    }
}
