package org.sysunit.mesh;

public class MockReplyCommand
    extends MockCommand
{

    public MockReplyCommand()
    {
        super();
    }

    public MockReplyCommand(int id)
    {
        super( id );
    }

    public void execute(Node node)
        throws Exception
    {
        super.execute( node );
        reply( new MockCommand( getId() + 1 ) );
    }
}
