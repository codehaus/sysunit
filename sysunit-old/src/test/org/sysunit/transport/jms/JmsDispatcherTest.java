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

import javax.jms.Destination;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.messenger.Messenger;
import org.apache.commons.messenger.MessengerManager;
import org.sysunit.command.Dispatcher;
import org.sysunit.transport.local.LocalDispatcherTest;

/**
 * A test case 
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class JmsDispatcherTest extends LocalDispatcherTest {

	protected String messengerName = "sysunitTopicConnection";
	protected String groupSubject = "SYSUNIT." + getClass().getName();
	protected Messenger messenger;
	protected Destination groupDestination;
	private Node node;

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(JmsDispatcherTest.class);
    }


 
	// Implementation methods
	//-------------------------------------------------------------------------                    

	protected void setUp() throws Exception {
		messenger = MessengerManager.get(messengerName);
		if (messenger == null) {
			fail("Could not find a messenger instance called: " + messengerName);
		}
        
		groupDestination = messenger.getDestination(groupSubject);
	}
	
   /**
     * Factory method to create a new dispatcher
     * @return a new dispatcher of commands to the given server
     */
    protected Dispatcher createDispatcher() throws Exception {
		// lets create a consumer of the command messages
		node = new Node(server, messenger, groupDestination);
		node.start();
				    	
        return new JmsDispatcher(messenger, groupDestination, node.getReplyToDestination());
    }
}
