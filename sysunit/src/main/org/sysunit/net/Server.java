package org.sysunit.net;

import org.sysunit.util.Barrier;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

public class Server
{
    private static final int SO_TIMEOUT = 500;

    private Object shouldRunLock;
    private boolean shouldRun;

    private ServerLoop serverLoop;

    private Thread[] threads;

    private Barrier startBarrier;
    private Barrier stopBarrier;

    private ServerSocket serverSocket;

    public Server(int numThreads,
                  ServerLoop serverLoop)
    {
        this.serverLoop = serverLoop;
        this.threads = new Thread[ numThreads ];

        this.startBarrier = new Barrier( numThreads + 1 );
        this.stopBarrier  = new Barrier( numThreads + 1 );

        this.shouldRunLock = new Object();
        this.shouldRun = false;
    }

    public ServerLoop getServerLoop()
    {
        return this.serverLoop;
    }

    public synchronized void start()
        throws Exception
    {
        this.serverSocket = new ServerSocket( 0 );
        this.serverSocket.setSoTimeout( SO_TIMEOUT );
        this.serverSocket.setReuseAddress( true );

        this.shouldRun = true;

        for ( int i = 0 ; i < this.threads.length ; ++i )
        {
            this.threads[ i ] = new ServerThread( "ServerThread-" + i,
                                                  this );
            this.threads[ i ].start();
        }

        blockOnStartBarrier();
    }

    public synchronized void stop()
        throws InterruptedException
    {
        //System.err.println( "Server::stop()" );
        synchronized ( this.shouldRunLock )
        {
            this.shouldRun = false;
        }

        for ( int i = 0 ; i < this.threads.length ; ++i )
        {
            this.threads[ i ].interrupt();
        }

        blockOnStopBarrier();

        try
        {
            this.serverSocket.close();
        }
        catch (IOException e)
        {
            // swallow
        }

        //System.err.println( "Server::stop() complete" );
    }

    public int getPort()
    {
        return this.serverSocket.getLocalPort();
    }

    void blockOnStartBarrier()
        throws InterruptedException
    {
        this.startBarrier.block();
    }

    void blockOnStopBarrier()
        throws InterruptedException
    {
        this.stopBarrier.block();
    }

    boolean shouldRun()
    {
        synchronized ( this.shouldRunLock )
        {
            return this.shouldRun;
        }
    }

    Socket accept()
        throws Exception
    {
        return this.serverSocket.accept();
    }
}

