/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit.command.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sysunit.command.Dispatcher;
import org.sysunit.command.MissingPropertyException;
import org.sysunit.command.Server;
import org.sysunit.command.master.TestNodeStartedCommand;
import org.sysunit.command.master.SyncCommand;
import org.sysunit.jelly.JvmRunner;
import org.sysunit.Synchronizer;
import org.sysunit.SynchronizationException;

/**
 * The Context for TestNodes
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class TestServer
    extends Server
    implements Synchronizer {
    private static final Log log = LogFactory.getLog(TestServer.class);

    private JvmRunner runner;
    private Dispatcher masterDispatcher;
    private String jvmName;
    private String xml;
    private Synchronizer synchronizer;

    public TestServer(String xml, String jvmName) {
        log.info( "test server " + jvmName + " with " + xml );
        this.xml = xml;
        this.jvmName = jvmName;
        this.synchronizer = new TestSynchronizer();
        this.runner = new JvmRunner( this );
    }

    public void start() throws Exception {
        log.info( "#######################################" );
        log.info( "starting test server " + getName() );
    	Dispatcher dispatcher = getMasterDispatcher();
    	if (dispatcher == null) {
    		throw new MissingPropertyException(this, "masterDispatcher");
    	}
        // start the test running
        getRunner().run(xml, jvmName);

        log.info( "tellilng master that " + getName() + " has " + getRunner().getManager().getNumSynchronizableTBeans() );

		getMasterDispatcher().dispatch(new TestNodeStartedCommand(getName(),
                                                                  getRunner().getManager().getNumSynchronizableTBeans()));

        getRunner().getManager().run();
    }

    // Properties
    //-------------------------------------------------------------------------    

    public JvmRunner getRunner() {
        return runner;
    }

    public void setRunner(JvmRunner runner) {
        this.runner = runner;
    }

    /**
     * @return the Dispatcher used to communicate with the Master server
     */
    public Dispatcher getMasterDispatcher() {
        return masterDispatcher;
    }

    /**
     * @param masterDispatcher
     */
    public void setMasterDispatcher(Dispatcher masterDispatcher) {
        this.masterDispatcher = masterDispatcher;
    }

    public void sync(String tbeanId,
                     String syncPointName)
        throws InterruptedException, SynchronizationException {
        log.info( "sync " + tbeanId + " on " + syncPointName + " on test server " + getName() );
        try {
            getMasterDispatcher().dispatch( new SyncCommand( tbeanId,
                                                             syncPointName ) );
        } catch (Exception e) {
            throw new SynchronizationException( e );
        }
        this.synchronizer.sync( tbeanId,
                                syncPointName );
    }

    public void unblockAll() {
        log.info( "unblocking all on " + getName() );
        getRunner().getSynchronizer().unblockAll();
    }

    public void registerSynchronizableTBean(String tbeanId) {
        log.info( "registering synchronizable tbean " + tbeanId + " with " + getName() );
    }

    public void unregisterSynchronizableTBean(String tbeanId) {
        log.info( "unregistering synchronizable tbean " + tbeanId + " with " + getName() );
    }

    public void error(String tbeanId) {
        log.info( "error in tbean " + tbeanId + " with " + getName() );
    }

}
