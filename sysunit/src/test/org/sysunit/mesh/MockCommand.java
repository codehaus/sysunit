package org.sysunit.mesh;

public class MockCommand
    extends Command
{
    private int id;


    public MockCommand()
    {
        this( 1 );
    }

    public MockCommand(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return this.id;
    }

    public void execute(Node node)
        throws Exception
    {
        ((MockNode)node).addMockCommand( this );
    }

    public int hashCode()
    {
        return this.id;
    }

    public boolean equals(Object thatObj)
    {
        MockCommand that = (MockCommand) thatObj;

        return this.id == that.id;
    }

    public String toString()
    {
        return "[MockCommand: id=" + this.id + "]";
    }
}
