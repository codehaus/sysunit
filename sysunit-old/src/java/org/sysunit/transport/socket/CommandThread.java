package org.sysunit.transport.socket;

import org.sysunit.command.Server;
import org.sysunit.command.Command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ObjectInputStream;
import java.io.InputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class CommandThread
    extends Thread {

    private static final Log log = LogFactory.getLog( CommandThread.class );

    private Server server;
    private ServerSocket serverSocket;

    public CommandThread(Server server,
                         ServerSocket serverSocket) {
        this.server       = server;
        this.serverSocket = serverSocket;
    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }

    public Server getServer() {
        return this.server;
    }

    public InetAddress getAddr() {
        return getServerSocket().getInetAddress();
    }

    public int getPort() {
        return getServerSocket().getLocalPort();
    }

    public void run() {
        while ( true ) {
            try {
                Socket client = getServerSocket().accept();
                handleClient( client );
            } catch (SocketTimeoutException e) {
                // swallow and loop
            } catch (Exception e) {
                log.error( "accept()",
                           e );
                break;
            }
        }
    }

    public void handleClient(Socket client)
        throws Exception {
        try {
            InputStream clientIn = client.getInputStream();
            ObjectInputStream objectIn = new ObjectInputStream( clientIn );

            SocketMessage message = (SocketMessage) objectIn.readObject();

            log.debug( "received: " + message );

            Object payload = message.getPayload();

            if ( payload instanceof Command ) {
                Command command = (Command) payload;
                log.debug( "command: " + command.getClass().getName() + " // " + command );
                command.setReplyDispatcher( new SocketDispatcher( this,
                                                                  message.getReplyToAddr(),
                                                                  message.getReplyToPort() ) );

                command.run( getServer() );
            } else {
                log.debug( "unknown payload: " + payload );
            }
        } finally {
            client.close();
        }
    }
}
