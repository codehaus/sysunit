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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sysunit.command.Server;
import org.sysunit.transport.jms.TestNode;

/**
 * The Slave server which spawns another JVM to run each TestNode
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class SlaveServer extends Server {
    private static final Log log = LogFactory.getLog(SlaveServer.class);

    private boolean forkJvm = false;

    public SlaveServer() {
    }

    /**
     * @param command
     */
    public void launchTestNode(LaunchTestNodeCommand command) {
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
    private void launchNodeInForkedJvm(LaunchTestNodeCommand command) {
        String[] args = getTestArguments(command);

        ProcessRunner runner = ProcessRunner.newJavaProcess(TestNode.class, args);
        Thread thread = new Thread(runner);
        thread.start();
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
