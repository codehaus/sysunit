package org.sysunit.command.master;

import org.sysunit.AlreadySynchronizedException;
import org.sysunit.command.State;
import org.sysunit.command.StateCommand;
import org.sysunit.command.test.UnblockAllCommand;
import org.sysunit.command.test.RunTestCommand;
import org.sysunit.command.test.KillCommand;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Set;
import java.util.HashSet;

public class RunState
    extends MasterState {

	private static final Log log = LogFactory.getLog(RunState.class);

    public static abstract class Command
        extends StateCommand {

        public void run(State state)
            throws Exception {
            if ( state instanceof RunState ) {
                run( (RunState) state );
            } else {
                throw new Exception( "not in run state: " + this.getClass().getName() + " vs " + state.getClass().getName() );
            }
        }

        public abstract void run(RunState state)
            throws Exception;
    }

    private TestNodeInfo[] testNodeInfos;

    private int numSyncTBeans;
    private int numActiveSyncTBeans;
    private Set waitingTBeans;
    private Set ranTestServers;
    private WatchdogThread watchdog;

    public RunState(MasterServer server,
                    TestNodeInfo[] testNodeInfos,
                    long watchdogTimeout) {
        super( server );

        this.testNodeInfos = testNodeInfos;

        for ( int i = 0 ; i < testNodeInfos.length ; ++i ) {
            this.numSyncTBeans += testNodeInfos[ i ].getNumSynchronizableTBeans();
            this.numActiveSyncTBeans = this.numSyncTBeans;
        }

        this.waitingTBeans   = new HashSet();
        this.ranTestServers  = new HashSet();
        this.watchdog        = new WatchdogThread( watchdogTimeout,
                                                   this );
    }

    public WatchdogThread getWatchdog() {
        return this.watchdog;
    }

    public void enter()
        throws Exception {
        for ( int i = 0 ; i < this.testNodeInfos.length ; ++i ) {
            this.testNodeInfos[ i ].getDispatcher().dispatch( new RunTestCommand() );
        }
        this.watchdog.start();
    }

    public synchronized void sync(String testServerName,
                                  String tbeanId,
                                  String syncPointName)
        throws Exception
    {
        log.debug( "SYNC: " + tbeanId + " on " + syncPointName );
        if ( this.waitingTBeans.contains( tbeanId ) ) {
            throw new AlreadySynchronizedException ( tbeanId,
                                                     syncPointName );
        }

        this.waitingTBeans.add( tbeanId );

        checkUnblock();
    }

    protected void checkUnblock()
        throws Exception {
        log.debug( "SHOULD UNBLOCK CHECK" );
        if ( shouldUnblock() ) {
            unblockAll();
        } 
    }

    protected boolean shouldUnblock() {
        log.debug( "CHECK: " + this.waitingTBeans.size() + " vs " + this.numActiveSyncTBeans );
        return this.waitingTBeans.size() == this.numActiveSyncTBeans;
    }

    protected void unblockAll()
        throws Exception {
        log.debug( "UNBLOCK ALL" );
        this.waitingTBeans.clear();
        for ( int i = 0 ; i < testNodeInfos.length ; ++i ) {
            testNodeInfos[ i ].getDispatcher().dispatch( new UnblockAllCommand() );
        }
    }

    public synchronized void registerSynchronizableTBean(String testServerName,
                                                         String tbeanId)
        throws Exception {
        ++this.numActiveSyncTBeans;
    }

    public synchronized void unregisterSynchronizableTBean(String testServerName,
                                                           String tbeanId)
        throws Exception {
        log.debug( "UNREGISTER: " + tbeanId );
        this.waitingTBeans.remove( tbeanId );
        --this.numActiveSyncTBeans;
        checkUnblock();
    }

    public void tbeansRan(String testServerName)
        throws Exception {
        log.debug( "TBEANS RAN " + testServerName );
        this.ranTestServers.add( testServerName );

        if ( this.ranTestServers.size() == testNodeInfos.length ) {
            getWatchdog().cancel();
            getServer().exitState( this );
        }
    }

    public void tbeanError(String testServerName,
                           String tbeanId)
        throws Exception {
        getWatchdog().cancel();
        kill();
    }

    public void kill()
        throws Exception {
        for ( int i = 0 ; i < testNodeInfos.length ; ++i ) {
            testNodeInfos[ i ].getDispatcher().dispatch( new KillCommand() );
        }
        getServer().exitState( this );
    }

    public TestNodeInfo[] getTestNodeInfos() {
        return this.testNodeInfos;
    }
}
