package org.sysunit.command.master;

import org.sysunit.command.State;
import org.sysunit.command.StateCommand;
import org.sysunit.command.test.TearDownTBeansCommand;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class TearDownState
    extends MasterState {

    private static final Throwable[] EMPTY_THROWABLE_ARRAY = new Throwable[0];

    public static abstract class Command
        extends StateCommand {

        public void run(State state)
            throws Exception {
            if ( state instanceof TearDownState ) {
                run( (TearDownState) state );
            }
        }

        public abstract void run(TearDownState state)
            throws Exception;

    }

    private TestNodeInfo[] testNodeInfos;
    private Set tornDownTestServers;
    private List errors;

    public TearDownState(MasterServer server,
                         TestNodeInfo[] testNodeInfos) {
        super( server );
        this.testNodeInfos       = testNodeInfos;
        this.tornDownTestServers = new HashSet();
        this.errors              = new ArrayList();
    }

    public void enter()
        throws Exception {
        for ( int i = 0 ; i < this.testNodeInfos.length ; ++i ) {
            this.testNodeInfos[ i ].getDispatcher().dispatch( new TearDownTBeansCommand() );
        }
    }

    public void testServerTornDown(String testServerName,
                                   Throwable[] errors)
        throws Exception {
        this.tornDownTestServers.add( testServerName );

        for ( int i = 0 ; i < errors.length ; ++i ) {
            this.errors.add( errors[ i ] );
        }

        if ( this.tornDownTestServers.size() == this.testNodeInfos.length ) {
            getServer().exitState( this );
        }
    }

    public TestNodeInfo[] getTestNodeInfos() {
        return this.testNodeInfos;
    }

    public Throwable[] getErrors() {
        return (Throwable[]) this.errors.toArray( EMPTY_THROWABLE_ARRAY );
    }
}
