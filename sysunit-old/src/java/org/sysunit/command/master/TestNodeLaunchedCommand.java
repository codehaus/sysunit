/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit.command.master;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This Command is sent when a new TestNode starts up
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class TestNodeLaunchedCommand
    extends MasterCommand {

    private static final Log log = LogFactory.getLog(TestNodeStartedCommand.class);

    private String name;
    private int numSynchronizableTBeans;

    public TestNodeLaunchedCommand(String name,
                                   int numSynchronizableTBeans) {
		this.name = name;
        this.numSynchronizableTBeans = numSynchronizableTBeans;
   }

    public void run(MasterServer masterServer) throws Exception {
        log.info( "running TestNodeStartedCommand for " + name );
    	masterServer.addTestNode(this);
    }
    
    public String getName() {
        return name;
    }

    public int getNumSynchronizableTBeans() {
        return this.numSynchronizableTBeans;
    }
}
