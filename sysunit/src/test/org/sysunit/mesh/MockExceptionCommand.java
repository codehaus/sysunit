package org.sysunit.mesh;

public class MockExceptionCommand
    extends MockCommand
{

    public MockExceptionCommand()
    {
        super();
    }

    public MockExceptionCommand(int id)
    {
        super( id );
    }

    public void execute(Node node)
        throws Exception
    {
        super.execute( node );

        throw new MockCommandException( this );
    }
}
