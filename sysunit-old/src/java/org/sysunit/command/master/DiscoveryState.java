package org.sysunit.command.master;

import org.sysunit.command.State;
import org.sysunit.command.StateCommand;
import org.sysunit.command.Dispatcher;
import org.sysunit.command.slave.RequestMembersCommand;

import java.util.Set;
import java.util.HashSet;

public class DiscoveryState
    extends MasterState {

    public static abstract class Command
        extends StateCommand {

        public void run(State state)
            throws Exception {
            if ( state instanceof DiscoveryState ) {
                run ( (DiscoveryState) state );
            }
        }

        public abstract void run(DiscoveryState state)
            throws Exception;
    }

    private Dispatcher slaveGroupDispatcher;
    private long discoveryWait;
    private Set slaveNodeInfos;

    public DiscoveryState(MasterServer server,
                          Dispatcher slaveGroupDispatcher,
                          long discoveryWait) {
        super( server );
        this.slaveGroupDispatcher = slaveGroupDispatcher;
        this.discoveryWait        = discoveryWait;
        this.slaveNodeInfos       = new HashSet();
    }

    public void enter()
        throws Exception {
        this.slaveGroupDispatcher.dispatch( new RequestMembersCommand() );
        Thread.sleep( this.discoveryWait );

        // getServer().exitState( this );
    }

    public void addSlaveNode(String slaveNodeName,
                             Dispatcher dispatcher) {
        
        SlaveNodeInfo slaveNodeInfo = new SlaveNodeInfo( slaveNodeName,
                                                         dispatcher );

        this.slaveNodeInfos.add( slaveNodeInfo );
    }

    public SlaveNodeInfo[] getSlaveNodeInfos() {
        return (SlaveNodeInfo[]) this.slaveNodeInfos.toArray( SlaveNodeInfo.EMPTY_ARRAY );
    }

}
