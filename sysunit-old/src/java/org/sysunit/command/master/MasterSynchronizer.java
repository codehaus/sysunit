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

    public synchronized void sync(String tbeanId,
                                  String syncPointName)
        throws SynchronizationException, DispatchException {

        log.info( "synchronizing " + tbeanId + " on " + syncPointName );

        if ( this.waitingTBeans.containsKey( tbeanId ) ) {
            throw new AlreadySynchronizedException( tbeanId,
                                                    (String) this.waitingTBeans.get( tbeanId ) );
        }

        this.waitingTBeans.put( tbeanId,
                                syncPointName );

        if ( waitingTBeans.size() == numTBeans ) {
            unblockAll();
        }
    }

    public synchronized void addTestNode(TestNodeInfo testNodeInfo) {
        this.numTBeans += testNodeInfo.getNumSynchronizableTBeans();
        this.dispatchers.add( testNodeInfo.getDispatcher() );
        log.info( "adding test node: " + testNodeInfo );
    }

    protected void unblockAll()
        throws DispatchException {
        log.info( "unblocking all" );
        for ( Iterator dispatcherIter = this.dispatchers.iterator();
              dispatcherIter.hasNext(); ) {
            Dispatcher dispatcher = (Dispatcher) dispatcherIter.next();

            dispatcher.dispatch( new UnblockAllCommand() );
        }

        this.waitingTBeans.clear();
    }
}
