package org.sysunit.command.master;

import org.sysunit.command.State;
import org.sysunit.command.StateCommand;
import org.sysunit.command.test.SetUpTBeansCommand;

import java.util.Set;
import java.util.HashSet;

public class SetUpState
    extends MasterState {

    public static abstract class Command
        extends StateCommand {

        public void run(State state)
            throws Exception {
            if ( state instanceof SetUpState ) {
                run ( (SetUpState) state );
            } else {
                throw new Exception( "no in SetUpState" );
            }
        }

        public abstract void run(SetUpState state)
            throws Exception;
    }

    private TestNodeInfo[] testNodeInfos;
    private Set setUpTestServers;

    public SetUpState(MasterServer server,
                      TestNodeInfo[] testNodeInfos) {
        super( server );
        this.testNodeInfos    = testNodeInfos;
        this.setUpTestServers = new HashSet();
    }

    public void enter()
        throws Exception {
        for ( int i = 0 ; i < this.testNodeInfos.length ; ++i ) {
            this.testNodeInfos[ i ].getDispatcher().dispatch( new SetUpTBeansCommand() );
        }
    }

    public void testServerSetUp(String testServerName)
        throws Exception {
        this.setUpTestServers.add( testServerName );

        if ( this.setUpTestServers.size() == this.testNodeInfos.length ) {
            getServer().exitState( this );
        }
    }

    public TestNodeInfo[] getTestNodeInfos() {
        return this.testNodeInfos;
    }
}
