package org.sysunit.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerThread
    extends Thread
{
    private Server server;

    public ServerThread(Server server)
    {
        super( "ServerThread" );
        this.server = server;
    }

    public Server getServer()
    {
        return this.server;
    }

    public void run()
    {
        try
        {
            getServer().blockOnStartBarrier();
        }
        catch (InterruptedException e)
        {
            return;
        }

        int loop = 0;

      LOOP:
        while ( this.server.shouldRun() )
        {
            try
            {
                Socket socket = getServer().accept();
                
                try
                {
                    getServer().getServerLoop().accept( socket.getInetAddress(),
                                                        socket.getPort(),
                                                        socket.getInputStream(),
                                                        socket.getOutputStream() );
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    try
                    {
                        socket.close();
                    }
                    catch (IOException e)
                    {
                        // swallow
                    }
                }
            }
            catch (SocketTimeoutException e)
            {
                // loop
            }
            catch (Exception e)
            {
                break LOOP;
            }
            
        }

        try
        {
            getServer().blockOnStopBarrier();
        }
        catch (InterruptedException e)
        {
            return;
        }
    }
}
