package org.sysunit.mesh;

import java.util.List;
import java.util.ArrayList;

public class MockNode
    extends Node
{
    private static final MockCommand[] EMPTY_MOCKCOMMAND_ARRAY = new MockCommand[0];
    private List commands;
    private List exceptionCommands;
    private List completedCommands;

    public MockNode(String name)
    {
        super( name );
        this.commands = new ArrayList();
        this.exceptionCommands = new ArrayList();
        this.completedCommands = new ArrayList();
    }

    public void addMockCommand(MockCommand command)
    {
        this.commands.add( command );
    }

    public Command[] getExecutedCommands()
    {
        return (Command[]) this.commands.toArray( EMPTY_MOCKCOMMAND_ARRAY );
    }

    public Command[] getExceptionCommands()
    {
        return (Command[]) this.exceptionCommands.toArray( EMPTY_MOCKCOMMAND_ARRAY );
    }

    public Command[] getCompletedCommands()
    {
        return (Command[]) this.completedCommands.toArray( EMPTY_MOCKCOMMAND_ARRAY );
    }

    void reportException(int uid, 
                         Exception exception)
    {
        this.exceptionCommands.add( uid + "" );
    }

    void reportCompletion(int uid)
    {
        this.completedCommands.add( uid + "" );
    }
                         
}
