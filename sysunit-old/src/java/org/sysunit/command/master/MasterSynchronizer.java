package org.sysunit.command.master;

import org.sysunit.SynchronizationException;
import org.sysunit.AlreadySynchronizedException;
import org.sysunit.command.Dispatcher;
import org.sysunit.command.DispatchException;
import org.sysunit.command.test.UnblockAllCommand;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class MasterSynchronizer {

    private static final Log log = LogFactory.getLog(MasterSynchronizer.class);

    private int numTBeans;
    private List dispatchers;
    private Map waitingTBeans;

    public MasterSynchronizer() {
        this.dispatchers = new ArrayList();
        this.waitingTBeans = new HashMap();
    }

    public void sync(String tbeanId,
                     String syncPointName)
        throws SynchronizationException, DispatchException {

        log.debug( "* * * * * * * * * * * * synchronizing " + tbeanId + " on " + syncPointName );

        if ( this.waitingTBeans.containsKey( tbeanId ) ) {
            throw new AlreadySynchronizedException( tbeanId,
                                                    (String) this.waitingTBeans.get( tbeanId ) );
        }

        this.waitingTBeans.put( tbeanId,
                                syncPointName );

        checkUnblockAll();
    }

    public void addTestNode(TestNodeInfo testNodeInfo) {
        this.dispatchers.add( testNodeInfo.getDispatcher() );
        log.debug( "adding test node: " + testNodeInfo );
    }

    protected void unblockAll()
        throws DispatchException {
        log.debug( "* * * * * unblocking all " + numTBeans );
        for ( Iterator dispatcherIter = this.dispatchers.iterator();
              dispatcherIter.hasNext(); ) {
            Dispatcher dispatcher = (Dispatcher) dispatcherIter.next();

            dispatcher.dispatch( new UnblockAllCommand() );
        }

        this.waitingTBeans.clear();

        log.debug( "* * * * after unblock all" + numTBeans );
    }

    protected void checkUnblockAll()
        throws DispatchException {
        log.debug( "checking unblock on " + this.waitingTBeans + " vs " + this.numTBeans );
        if ( this.waitingTBeans.size() == this.numTBeans ) {
            unblockAll();
        }
    }

    public void registerSynchronizableTBean(String tbeanId)
        throws DispatchException {
        ++this.numTBeans;
    }

    public void unregisterSynchronizableTBean(String tbeanId)
        throws DispatchException {
        --this.numTBeans;
        this.waitingTBeans.remove( tbeanId );
        checkUnblockAll();
    }

    public void error(String tbeanId)
        throws DispatchException {
        this.numTBeans = 0;
        unblockAll();
    }
}
