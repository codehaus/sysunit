/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit.transport.jms;

import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.messenger.Messenger;
import org.apache.commons.messenger.MessengerManager;
import org.sysunit.mock.MockMasterServer;
import org.sysunit.mock.MockSlaveServer;
import org.sysunit.mock.MockTestServer;

/**
 * A test case 
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class MockServersTest extends TestCase {

    protected String messengerName = "sysunitTopicConnection";
    protected String masterGroupSubject = "SYSUNIT.MASTERS";
    protected String slaveGroupSubject = "SYSUNIT.SLAVES";
	
	protected MockMasterServer masterServer = new MockMasterServer();
	protected MockSlaveServer slaveServer = new MockSlaveServer();
	protected MockTestServer testServer = new MockTestServer();
	
	protected MasterNode masterNode;
	protected SlaveNode slaveNode;
	protected TestNode testNode;
	
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(MockServersTest.class);
    }

    public void testMasterAndSlave() throws Exception {
        // lets see if the master gets a new slave
        Thread.sleep(1000);
        
        Map members = masterServer.getMemberMap();
        assertEquals("Membership size of: " + members, 1, members.size());
    }

    // Implementation methods
    //-------------------------------------------------------------------------                    

    protected void setUp() throws Exception {
        Messenger messenger = MessengerManager.get(messengerName);
        if (messenger == null) {
            fail("Could not find a messenger instance called: " + messengerName);
        }
        
        slaveNode = new SlaveNode(slaveServer, messenger, messenger.getDestination(slaveGroupSubject));
        slaveNode.start();

        masterNode =
            new MasterNode(masterServer, messenger, messenger.getDestination(masterGroupSubject), messenger.getDestination(slaveGroupSubject));
		masterNode.start();
    }

}
