package org.sysunit.mesh;

public class MockCommandException
    extends Exception
{
    private MockCommand command;

    public MockCommandException(Command cause)
    {
        this.command = command;
    }

    public MockCommand getCommand()
    {
        return this.command;
    }
}
