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
// import org.sysunit.transport.jms.TestNode;

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

    private Map masterSessions;

    public SlaveServer() {
        this.masterSessions = new HashMap();
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
        
        /*
        Thread thread = new Thread() {
                public void run() {
                    TestNode.main(args);
                }
            };

        thread.start();
        */
    }

    /**
     * Runs the Test logical machine in a new forked JVM
     * @param command
     */
    private synchronized void launchNodeInForkedJvm(LaunchTestNodeCommand command)
        throws Exception {

        MasterSession session = getMasterSession( command );

        log.info( "using session " + session );

        session.addTestNode( getTestArguments( command ) );
    }

    protected synchronized MasterSession getMasterSession(LaunchTestNodeCommand command)
        throws Exception {

        if ( this.masterSessions.containsKey( command.getMasterID() ) ) {
            return (MasterSession) this.masterSessions.get( command.getMasterID() );
        }

        String mungedId = sanitizePath( command.getMasterID() );

        File dir = new File( this.tmpDir,
                             mungedId );

        MasterSession session = new MasterSession( command.getReplyDispatcher(),
                                                   dir,
                                                   command.getJarMap(),
                                                   this );

        this.masterSessions.put( command.getMasterID(),
                                 session );

        return session;
        
    }

    protected synchronized MasterSession lookupMasterSession(String masterID)
        throws Exception {
        return (MasterSession) this.masterSessions.get( masterID );
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

    /**
     * Creates the command line arguments to the TestNode JVM
     * 
     * @param command
     * @return
     */
    protected String[] getTestArguments(LaunchTestNodeCommand command) {
        // the TestNode needs to know the destination of the Master
        String destination = command.getReplyDispatcher().toString();

        String[] args = { destination, command.getXml(), command.getJvmName()};
        return args;
    }

    public void storeJar(String masterID,
                         String jarName,
                         byte[] bytes)
        throws Exception {
        lookupMasterSession( masterID ).storeJar( jarName,
                                                  bytes );
    }

}
