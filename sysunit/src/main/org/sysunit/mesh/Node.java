package org.sysunit.mesh;

import org.sysunit.util.ThreadPool;

import java.io.Serializable;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.HashMap;

public class Node
{
    private String name;
    private LocalNodeInfo localNodeInfo;

    private int uidCounter;

    private ThreadPool pool;

    private Map inFlightCommands;

    public Node(String name)
    {
        this.name          = name;
        this.localNodeInfo = new LocalNodeInfo( this,
                                                this );
        this.uidCounter    = 0;

        this.pool = new ThreadPool( 4 );

        this.inFlightCommands = new HashMap();
    }

    public String getName()
    {
        return this.name;
    }

    public LocalNodeInfo getLocalNodeInfo()
    {
        return this.localNodeInfo;
    }

    public void start()
        throws Exception
    {
        this.pool.start();
    }

    public void stop()
        throws InterruptedException
    {
        this.pool.stop();
    }
    
    public void executeOnSelf(Command command)
        throws Exception
    {
        executeOn( getLocalNodeInfo(),
                   command );
    }

    public int getPort()
    {
        return -1;
    }

    public int executeOn(NodeInfo destination,
                         Command command)
        throws Exception
    {
        Command dupeCommand = duplicate( command );

        dupeCommand.setUid( getNextUid() );

        addInFlightCommand( dupeCommand );

        if ( destination instanceof LocalNodeInfo )
        {
            ((LocalNodeInfo)destination).execute( getLocalNodeInfo(),
                                                  dupeCommand );
        }
        else
        {
            ((RemoteNodeInfo)destination).execute( dupeCommand );
        }

        return dupeCommand.getUid();
    }
    
    void execute(final NodeInfo origin,
                 final Command command)
    {
        this.pool.addTask( new Runnable()
            {
                public void run()
                {
                    command.wrapExecute( origin,
                                         Node.this );
                }
            } );
    }
    
    // ----------------------------------------------------------------------
    // ----------------------------------------------------------------------
    
    void reportException(int uid,
                         Exception exception)
    {
        Command command = getInFlightCommand( uid );

        System.err.println( "cause: " + command );
        System.err.println( "exception: " + exception );

        exception.printStackTrace();

        removeInFlightCommand( uid );
    }

    void reportCompletion(int uid)
    {
        removeInFlightCommand( uid );
    }

    void addInFlightCommand(Command command)
    {
        synchronized ( this.inFlightCommands )
        {
            this.inFlightCommands.put( command.getUid() + "",
                                       command );
        }
    }

    void removeInFlightCommand(int uid)
    {
        synchronized ( this.inFlightCommands )
        {
            this.inFlightCommands.remove( uid + "" );
            this.inFlightCommands.notifyAll();
        }
    }

    Command getInFlightCommand(int uid)
    {
        synchronized ( this.inFlightCommands )
        {
            return (Command) this.inFlightCommands.get( uid + "" );
        }
    }

    protected void waitFor(int uid)
        throws InterruptedException
    {
        synchronized ( this.inFlightCommands )
        {
            while ( this.inFlightCommands.containsKey( uid + "" ) )
            {
                this.inFlightCommands.wait();
            }
        }
    }

    int getNextUid()
    {
        return ++this.uidCounter;
    }

    Command duplicate(Command command)
        throws IOException, ClassNotFoundException
    {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream( byteOut );
        
        objectOut.writeObject( command );
        objectOut.close();
        
        byte[] bytes = byteOut.toByteArray();
        
        ByteArrayInputStream byteIn = new ByteArrayInputStream( bytes );
        ObjectInputStream objectIn = new ObjectInputStream( byteIn );
        
        Command dupeCommand = (Command) objectIn.readObject();
        objectIn.close();

        return dupeCommand;
    }

    public String toString()
    {
        return "[Node: name=" + this.name + "]";
    }

    protected CommandGroup newCommandGroup()
    {
        return new CommandGroup( this.inFlightCommands );
    }
}
