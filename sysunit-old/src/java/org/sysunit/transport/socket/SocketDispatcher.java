package org.sysunit.transport.socket;

import org.sysunit.command.Command;
import org.sysunit.command.Dispatcher;
import org.sysunit.command.DispatchException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class SocketDispatcher
    implements Dispatcher {

    private static final Log log = LogFactory.getLog( SocketDispatcher.class );

    private CommandThread commandThread;
    private InetAddress addr;
    private int port;

    public SocketDispatcher(CommandThread commandThread,
                            InetAddress addr,
                            int port) {
        this.commandThread = commandThread;
        this.addr = addr;
        this.port = port;
    }

    public CommandThread getCommandThread() {
        return this.commandThread;
    }

    public InetAddress getAddr() {
        return this.addr;
    }

    public int getPort() {
        return this.port;
    }

    public void start()
        throws Exception {

    }

    public void stop()
        throws Exception {

    }

    public void dispatch(Command command)
        throws DispatchException {

        try {
            Socket dest = new Socket( getAddr(),
                                      getPort() );
            
            try {
                OutputStream destOut = dest.getOutputStream();
                ObjectOutputStream objectOut = new ObjectOutputStream( destOut );

                SocketMessage message = new SocketMessage( this,
                                                           command );

                log.debug( "dispatching: " + message + " to " + this.addr + ":" + this.port );

                objectOut.writeObject( message );
            } catch (Exception e) {
                throw new DispatchException( command,
                                             e );
            } finally {
                dest.close();
            }
        } catch (Exception e) {
            throw new DispatchException( command,
                                         e );
        }

    }

    public String toString() {
        // return "[SocketDispatcher: addr=" + getAddr() + "; port=" + getPort() + "]";

        return getAddr().getHostAddress() + ":" + getPort();
    }
}
