package org.sysunit.mesh;

import java.io.Serializable;

public abstract class Command
    implements Serializable
{
    private transient Node thisNode;
    private transient NodeInfo origin;

    private int sequence;

    private static int sequenceCounter = 0;

    private int uid;

    public Command()
    {
        synchronized( Command.class )
        {
            this.sequence = ++this.sequenceCounter;
        }
    }

    public abstract void execute(Node node)
        throws Exception;

    void wrapExecute(NodeInfo origin,
                     Node thisNode)
    {
        setOrigin( origin );
        setThisNode( thisNode );

        boolean hadError = false;

        try
        {
            execute( thisNode );
        }
        catch (Throwable t)
        {
            if ( ! ( this instanceof ReportErrorCommand
                     ||
                     this instanceof ReportCompletionCommand ) )
            {
                try
                { 
                    getOrigin().reportError( getUid(),
                                             t );
                }
                catch (Exception e)
                {
                    System.err.println( "unable to report error from " + getThisNode().getLocalNodeInfo() + " to " + getOrigin() );
                    e.printStackTrace();
                }
            }
            else
            {
                System.err.println( "unable to report error from " + getThisNode().getLocalNodeInfo() + " to " + getOrigin() );
                t.printStackTrace();
            }
        }

        if ( ! ( this instanceof ReportErrorCommand
                 ||
                 this instanceof ReportCompletionCommand ) )
        {
            if ( ! hadError )
            {
                try
                {
                    getOrigin().reportCompletion( getUid() );
                }
                catch (Exception e)
                {
                    System.err.println( "unable to report completion from " + getThisNode().getLocalNodeInfo() + " to "  + getOrigin() );
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void reply(Command command)
        throws Exception
    {
        NodeInfo origin = getOrigin();

        if ( origin == null )
        {
            throw new Exception( "no known origin" );
        }

        //ReplyCommand reply = new ReplyCommand( this,
                                               //command );

        getThisNode().executeOn( getOrigin(),
                                 command );
    }

    void setOrigin(NodeInfo origin)
    {
        this.origin = origin;
    }

    public NodeInfo getOrigin()
    {
        return this.origin;
    }

    void setThisNode(Node thisNode)
    {
        this.thisNode = thisNode;
    }

    Node getThisNode()
    {
        return this.thisNode;
    }

    void setUid(int uid)
    {
        this.uid = uid;
    }

    int getUid()
    {
        return this.uid;
    }

    public boolean equals(Object thatObj)
    {
        Command that = (Command) thatObj;

        return this.getUid() == that.getUid();

        /*
        return  ( this.sequence == that.sequence 
                  &&
                  ( ( this.origin == null && that.origin == null )
                    ||
                    ( this.origin != null && this.origin.equals( that.origin ) ) ) );
        */
    }

    public int hashCode()
    {
        return getClass().hashCode() + getUid();
    }
}
