package org.sysunit.command;

public abstract class StateCommand
    extends Command {

    public void run(Server server)
        throws Exception {

        if ( server instanceof StateServer ) {
            run( (StateServer) server );
            return;
        }
            
        throw new Exception( "can only run state command upon state server." );
    }

    public void run(StateServer server)
        throws Exception
    {
        run( server.getState() );
    }

    public abstract void run(State state)
        throws Exception;
}
