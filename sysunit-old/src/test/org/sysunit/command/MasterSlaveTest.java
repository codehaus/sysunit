/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit.command;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.sysunit.mock.MockMasterServer;
import org.sysunit.mock.MockSlaveServer;
import org.sysunit.transport.local.LocalDispatcher;

/**
 * A test case 
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class MasterSlaveTest extends TestCase {

	protected MockMasterServer masterServer = new MockMasterServer();
	protected MockSlaveServer slaveServer = new MockSlaveServer();
	protected Dispatcher masterDispatcher;
	protected Dispatcher slaveDispatcher;
	
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(MasterSlaveTest.class);
    }

    public void testSimpleDispatch() throws Exception {
    }
    
	protected void setUp() throws Exception {
		masterDispatcher = createDispatcher(masterServer);
		slaveDispatcher = createDispatcher(masterServer);
		
		masterDispatcher.start();
		slaveDispatcher.start();
	}

    protected void tearDown() throws Exception {
		masterDispatcher.stop();
		slaveDispatcher.stop();
    }

	/**
	 * Factory method to create a new dispatcher
	 * @return a new dispatcher of commands to the given server
	 */
	protected Dispatcher createDispatcher(Server server) throws Exception {
		 return new LocalDispatcher(server);
	}
}
