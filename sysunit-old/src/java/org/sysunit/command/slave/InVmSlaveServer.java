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
 * A simple Slave server which runs the TestNode inside the JVM - which is not ideal
 * but allows a simple deployment. Typically InVmSlaveServer's JVM should be restarted after each
 * test.
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class InVmSlaveServer extends Server {
    private static final Log log = LogFactory.getLog(InVmSlaveServer.class);

    public InVmSlaveServer() {
    }

    /**
     * @param command
     */
    public void startTestNode(StartTestNodeCommand command) {

		String destination = "to be defined";
		
		String[] args = {destination, command.getXml(), command.getJvmName()};  
		
		// lets run the TestNode
		TestNode.main(args);  	
    }
}
