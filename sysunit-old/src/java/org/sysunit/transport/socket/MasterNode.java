package org.sysunit.transport.socket;

import org.sysunit.util.MultiThrowable;
import org.sysunit.command.master.MasterServer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetAddress;

public class MasterNode
    extends Node {

    private static final Log log = LogFactory.getLog( MasterNode.class );

    public static void main(String[] args)
        throws Throwable {

        if ( args.length != 1 ) {
            System.err.println( "usage: MasterNode <xml>" );
            System.exit( 1 );
        }

        int port = BeaconTransmitterThread.PORT;

        String sysunitPortStr = System.getProperty( "sysunit.port" );

        if ( sysunitPortStr != null ) {
            sysunitPortStr = sysunitPortStr.trim();

            if ( ! sysunitPortStr.equals( "" ) ) {
                port = Integer.parseInt( sysunitPortStr );
            }
        }

        MasterNode node = new MasterNode( InetAddress.getByName( "224.0.0.42" ),
                                          port,
                                          args[0] );

        node.start();

        Throwable[] errors = node.getErrors();

        if ( errors.length != 0 ) {
            throw new MultiThrowable( errors );
        }
            
    }

    private InetAddress beaconAddr;
    private int beaconPort;
    private String xml;
    private Throwable[] errors;

    public MasterNode(InetAddress beaconAddr,
                      int beaconPort,
                      String xml) {
        this.beaconAddr = beaconAddr;
        this.beaconPort = beaconPort;
        this.xml        = xml;
    }

    public InetAddress getBeaconAddr() {
        return this.beaconAddr;
    }

    public int getBeaconPort() {
        return this.beaconPort;
    }

    public String getXml() {
        return this.xml;
    }

    public Throwable[] getErrors() {
        return this.errors;
    }

    public void start()
        throws Exception {
        log.debug( "start()" );

        MasterServer server = new MasterServer( getXml() );

        CommandThread commandThread = createCommandThread( server );

        BeaconListenerThread beaconThread = new BeaconListenerThread( getBeaconAddr(),
                                                                      getBeaconPort(),
                                                                      10000 );

        beaconThread.setDaemon( false );
        beaconThread.start();
        beaconThread.join();

        SlaveNodeInfo[] slaveNodes = beaconThread.getSlaveNodes();

        if ( slaveNodes.length == 0 ) {
            throw new Exception( "no slave nodes available" );
        }

        SocketDispatcher[] slaveDispatchers = new SocketDispatcher[ slaveNodes.length ];

        for ( int i = 0 ; i < slaveNodes.length ; ++i ) {
            log.debug( "slave node: " + slaveNodes[i] );
            slaveDispatchers[i] = new SocketDispatcher( commandThread,
                                                        slaveNodes[i].getAddr(),
                                                        slaveNodes[i].getPort() );
        }

        server.setSlaveNodeDispatchers( slaveDispatchers );

        server.start();

        this.errors = server.waitFor();
    }
}
