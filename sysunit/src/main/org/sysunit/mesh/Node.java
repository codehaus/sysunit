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
import java.util.List;
import java.util.ArrayList;

public class Node
{
    private static final Throwable[] EMPTY_THROWABLE_ARRAY = new Throwable[0];

    private String name;
    private LocalNodeInfo localNodeInfo;

    private int uidCounter;

    private ThreadPool pool;

    private Map inFlightCommands;

    private List fundamentalErrors;

    public Node(String name)
    {
        this.name          = name;
        this.localNodeInfo = new LocalNodeInfo( this,
                                                this );
        this.uidCounter    = 0;

        this.pool = new ThreadPool( 4 );

        this.inFlightCommands = new HashMap();

        this.fundamentalErrors = new ArrayList();
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
        //System.err.println( "Node::stop()" );
        this.pool.stop();
        //System.err.println( "Node::stop() complete" );
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

    public Throwable[] getFundamentalErrors()
    {
        return (Throwable[]) this.fundamentalErrors.toArray( EMPTY_THROWABLE_ARRAY );
    }

    protected void addFundamentalError(Throwable error)
    {
        this.fundamentalErrors.add( error );
    }
    
    void reportError(int uid,
                     Throwable thrown)
    {
        Command command = getInFlightCommand( uid );

        this.fundamentalErrors.add( thrown );
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
