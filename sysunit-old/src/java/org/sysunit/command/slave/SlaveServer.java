/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit.command.slave;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sysunit.command.Server;
import org.sysunit.command.master.RequestJarCommand;
import org.sysunit.transport.jms.TestNode;

/**
 * The Slave server which spawns another JVM to run each TestNode
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class SlaveServer extends Server {
    private static final Log log = LogFactory.getLog(SlaveServer.class);

    private boolean forkJvm = true;

    private File tmpDir;

    private Map waiters;

    public SlaveServer() {
        this.waiters = new HashMap();
    }

    /**
     * @param command
     */
    public void launchTestNode(LaunchTestNodeCommand command)
        throws Exception {
        log.info(
            "About to start xml: "
            + command.getXml()
            + " with logical machine: "
            + command.getJvmName()
            + " from Master: "
            + command.getMasterID());

        if (isForkJvm()) {
            launchNodeInForkedJvm(command);
        }
        else {
            launchNodeLocally(command);
        }
    }

    public void start()
        throws Exception {

        this.tmpDir = File.createTempFile( "sysunit-",
                                           ".jars" );

        this.tmpDir.delete();

        this.tmpDir.mkdir();

        this.tmpDir.deleteOnExit();
        
        super.start();
    }

    // Properties
    //-------------------------------------------------------------------------    

    /**
     * @return
     */
    public boolean isForkJvm() {
        return forkJvm;
    }

    /**
     * @param forkJvm
     */
    public void setForkJvm(boolean forkJvm) {
        this.forkJvm = forkJvm;
    }

    // Implementation methods
    //-------------------------------------------------------------------------    

    /**
     * @param command
     */
    private void launchNodeLocally(LaunchTestNodeCommand command) {
        final String[] args = getTestArguments(command);

        // lets run the TestNode

        Thread thread = new Thread() {
                public void run() {
                    TestNode.main(args);
                }
            };

        thread.start();
    }

    /**
     * Runs the Test logical machine in a new forked JVM
     * @param command
     */
    private synchronized void launchNodeInForkedJvm(LaunchTestNodeCommand command)
        throws Exception {

        String mungedId = sanitizePath( command.getMasterID() );

        File dir = new File( this.tmpDir,
                             mungedId );

        LaunchWaiter waiter = new LaunchWaiter( dir,
                                                command,
                                                getTestArguments( command ) );

        log.debug( "PUT " + command.getMasterID() );

        this.waiters.put( command.getMasterID() + "-false",
                          waiter );

        waiter.start();
    }

    protected String sanitizePath(String path) {
        path = path.replace( File.pathSeparatorChar,
                             '@' );

        path = path.replace( '/',
                             '_' );

        path = path.replace( '\\',
                             '_' );

        path = path.replace( ':',
                             '_' );

        path = path.replace( '#',
                             '_' );

        path = path.replace( '$',
                             '_' );

        return path;
    }

    public synchronized void storeJar(String jarName,
                                      byte[] bytes,
                                      String masterId)
        throws Exception {

        LaunchWaiter waiter = (LaunchWaiter) this.waiters.get( masterId );

        log.debug( "GET " + masterId + " // " + waiter );

        waiter.storeJar( jarName,
                         bytes );
    }

    /**
     * Creates the command line arguments to the TestNode JVM
     * 
     * @param command
     * @return
     */
    protected String[] getTestArguments(LaunchTestNodeCommand command) {
        // the TestNode needs to know the destination of the Master
        String destination = command.getMasterID();

        String[] args = { destination, command.getXml(), command.getJvmName()};
        return args;
    }

}
