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
import org.sysunit.command.DispatchException;
import org.sysunit.command.MissingPropertyException;
import org.sysunit.command.Server;
import org.sysunit.command.master.TestNodeLaunchedCommand;
import org.sysunit.command.master.RegisterSynchronizableTBeanCommand;
import org.sysunit.command.master.UnregisterSynchronizableTBeanCommand;
import org.sysunit.command.master.TBeansSetUpCommand;
import org.sysunit.command.master.TBeansRanCommand;
import org.sysunit.command.master.TBeansDoneCommand;
import org.sysunit.command.master.SyncCommand;
import org.sysunit.jelly.JvmRunner;
import org.sysunit.util.Checkpoint;
import org.sysunit.util.CheckpointCallback;
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
    implements Synchronizer, CheckpointCallback {
    private static final Log log = LogFactory.getLog(TestServer.class);

    private JvmRunner runner;
    private Dispatcher masterDispatcher;
    private String jvmName;
    private String xml;
    private Synchronizer synchronizer;

    private int syncCounter;
    private Object syncLock;

    public TestServer(String xml, String jvmName) {
        log.info( "test server " + jvmName + " with " + xml + " is " + getName() );
        this.xml = xml;
        this.jvmName = jvmName;
        this.synchronizer = new TestSynchronizer();
        this.runner = new JvmRunner( this,
                                     this,
                                     this,
                                     this );

        this.syncLock = new Object();
    }

    public void start() throws Exception {
        log.info( "#######################################" );
        log.info( "starting test server " + getName() );
    	Dispatcher dispatcher = getMasterDispatcher();
        this.runner.setTestServerName( getName() );
    	if (dispatcher == null) {
    		throw new MissingPropertyException(this, "masterDispatcher");
    	}
        // start the test running
        getRunner().run(xml, jvmName);

        log.info( "tellilng master that " + getName() + " has " + getRunner().getManager().getNumSynchronizableTBeans() );

		getMasterDispatcher().dispatch(new TestNodeLaunchedCommand(getName(),
                                                                   getRunner().getManager().getNumSynchronizableTBeans()));

        //getRunner().getManager().run();
    }


    public void setUpTBeans() throws Exception {
        getRunner().getManager().setUpTBeans();
    }

    public void runTest() throws Exception {
        getRunner().getManager().runTest();
    }

    public void tearDownTBeans() throws Exception {
        getRunner().getManager().tearDownTBeans();
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

        int counter = 0;

        synchronized ( this.syncLock ) {
            counter = this.syncCounter;
        }

        try {
            getMasterDispatcher().dispatch( new SyncCommand( getName(),
                                                             tbeanId,
                                                             syncPointName ) );
        } catch (Exception e) {
            throw new SynchronizationException( e );
        }

        synchronized ( this.syncLock ) {
            if ( counter != this.syncCounter ) {
                log.info( "test-server: unblocking inside sync on " + getName() );
                this.synchronizer.unblockAll();
                return;
            }
        }

        log.info( "test-server: syncing inside sync on " + getName() );
        synchronizer.sync( tbeanId,
                           syncPointName );
    }

    public synchronized void unblockAll() {
        log.info( "test-server: unblocking all on " + getName() );

        synchronized ( this.syncLock ) {
            ++this.syncCounter;
            this.synchronizer.unblockAll();
        }
        log.info( "test-server: unblocked all on " + getName() );
    }

    public void registerSynchronizableTBean(String tbeanId)
        throws SynchronizationException {
        log.info( "registering synchronizable tbean " + tbeanId + " with " + getName() );
        try {
            getMasterDispatcher().dispatch( new RegisterSynchronizableTBeanCommand( getName(),
                                                                                    tbeanId ) );
        } catch (DispatchException e) {
            throw new SynchronizationException( e );
        }
    }

    public void unregisterSynchronizableTBean(String tbeanId)
        throws SynchronizationException {
        log.info( "unregistering synchronizable tbean " + tbeanId + " with " + getName() );
        try {
            getMasterDispatcher().dispatch( new UnregisterSynchronizableTBeanCommand( getName(),
                                                                                      tbeanId ) );
        } catch (DispatchException e) {
            throw new SynchronizationException( e );
        }
    }

    public void error(String tbeanId) {
        log.info( "error in tbean " + tbeanId + " with " + getName() );
    }

    public void notify(Checkpoint checkpoint)
        throws Exception {
        log.info( "################## &&&&&&&  ########  checkpoint: " + checkpoint.getName() );
        if ( checkpoint.getName().equals( "begin" ) ) {
            getMasterDispatcher().dispatch( new TBeansSetUpCommand( getName() ) );
        } else if ( checkpoint.getName().equals( "end" ) ) {
            getMasterDispatcher().dispatch( new TBeansRanCommand( getName() ) );
        } else if ( checkpoint.getName().equals( "done" ) ) {
            Throwable[] errors = getRunner().getManager().collectErrors();
            getMasterDispatcher().dispatch( new TBeansDoneCommand( getName(),
                                                                   errors ) );
        } else {
            log.error( "unknown checkpoint: " + checkpoint.getName() );
        }

    }

}
