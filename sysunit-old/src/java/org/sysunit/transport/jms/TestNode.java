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
import javax.jms.JMSException;

import org.apache.commons.messenger.Messenger;
import org.apache.commons.messenger.MessengerManager;
import org.sysunit.command.Dispatcher;
import org.sysunit.command.master.TestNodeStartedCommand;
import org.sysunit.command.test.TestServer;

/**
 * A Test Node implementation via JMS which on startup advertises itself to the Master
 * and then starts running the test case
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class TestNode extends Node {

    private TestServer server;
    private Dispatcher masterDispatcher;
    private String jvmName;
    private String xml;

    public static void main(String[] args) {
        // lets assume the messenger.xml is on the classpath
        String messengerName = "topicConnection";
        String groupSubject = "SYSUNIT.TESTNODES";
        if (args.length <= 3) {
        	System.out.println("Usage: <masterSubject> <xmlURI> <jvmName> [<groupSubject>]");
        	return;
        }
        String masterSubject = args[0];
        String xml = args[1];
        String jvmName = args[2];
        if (args.length > 3) {
			groupSubject = args[3];
        }

        try {
            Messenger messenger = MessengerManager.get(messengerName);
            if (messenger == null) {
                System.out.println("Could not find a messenger instance called: " + messengerName);
                return;
            }
			Destination masterDestination = messenger.getDestination(masterSubject);
			Destination groupDestination = messenger.getDestination(groupSubject);
            TestNode controller = new TestNode(new TestServer(), messenger, masterDestination, groupDestination, xml, jvmName);
            controller.start();
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public TestNode(TestServer server, Messenger messenger, Destination masterDestination, Destination testGroupDestination, String xml, String jvmName) throws JMSException {
    	super(server, messenger, testGroupDestination);
		this.server = server;    	
    	this.xml = xml;
    	this.jvmName = jvmName;
    	
		masterDispatcher = new JmsDispatcher(messenger, masterDestination, getReplyToDestination());
    }

    public void start() throws Exception {
    	masterDispatcher.dispatch(new TestNodeStartedCommand(server.getName()));
    	
    	// start the test running
    	server.run(xml, jvmName);
    }
}
