package org.sysunit.transport.socket;

import org.sysunit.command.Server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Date;

public class Node {

    public CommandThread createCommandThread(Server server)
        throws Exception {

        ServerSocket serverSocket = new ServerSocket( 0 );
        serverSocket.setSoTimeout( 1000 );

        CommandThread thread = new CommandThread( server,
                                                  serverSocket );

        server.setName( new Date().getTime() + "." + serverSocket.getLocalPort() );

        thread.setDaemon( true );

        thread.start();

        Thread.sleep( 1000 );

        return thread;
    }
}
