package org.sysunit.transport.socket;

import org.sysunit.command.test.TestServer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetAddress;

public class TestNode
    extends Node {

    private static final Log log = LogFactory.getLog( TestNode.class );

    public static void main(String[] args)
        throws Exception {

        if ( args.length != 3 ) {
            System.err.println( "usage: TestNode <masteraddr:masterport> <xml> <jvmname>" );
            System.exit( 1 );
        }

        String addrPort = args[0];
        String xml = args[1];
        String jvmName = args[2];

        String addrStr = addrPort.substring( 0,
                                             addrPort.indexOf( ":" ) ).trim();

        String portStr = addrPort.substring( addrPort.indexOf( ":" ) + 1 ).trim();

        int masterPort = Integer.parseInt( portStr );

        InetAddress masterAddr = InetAddress.getByName( addrStr );

        TestNode node = new TestNode( masterAddr,
                                      masterPort,
                                      xml,
                                      jvmName );

        node.start();
    }

    private InetAddress masterAddr;
    private int masterPort;

    private String xml;
    private String jvmName;

    public TestNode(InetAddress masterAddr,
                    int masterPort,
                    String xml,
                    String jvmName) {
        this.masterAddr = masterAddr;
        this.masterPort = masterPort;
        this.xml        = xml;
        this.jvmName    = jvmName;
    }

    public InetAddress getMasterAddr() {
        return this.masterAddr;
    }

    public int getMasterPort() {
        return this.masterPort;
    }

    public String getXml() {
        return this.xml;
    }

    public String getJvmName() {
        return this.jvmName;
    }

    public void start()
        throws Exception {

        TestServer server = new TestServer( getXml(),
                                            getJvmName() );


        CommandThread commandThread = createCommandThread( server );

        server.setMasterDispatcher( new SocketDispatcher( commandThread,
                                                          getMasterAddr(),
                                                          getMasterPort() ) );

        server.start();

        log.debug( "WAIT FOR" );
        server.waitFor();
        log.debug( "DONE WITH WAIT FOR" );

        System.err.println( "EXITING EXITING EXITING" );
        System.exit( 0 );
    }
}
