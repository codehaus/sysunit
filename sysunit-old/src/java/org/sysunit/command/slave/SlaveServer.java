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

/**
 * The Slave server which spawns another JVM to run each TestNode
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class SlaveServer extends Server {
    private static final Log log = LogFactory.getLog(SlaveServer.class);

    public SlaveServer() {
    }

    /**
     * @param command
     */
    public void startTestNode(StartTestNodeCommand command) {
        ProcessRunner runner = ProcessRunner.newInstance( 
                                                  new String[] {
                                                      org.sysunit.jelly.JvmRunner.class.getName(),
                                                      command.getXml(),
                                                      command.getJvmName()
                                                  } );

        Thread thread = new Thread( runner );

        thread.start();
    }
}
