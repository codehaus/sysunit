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
import org.sysunit.command.master.MasterServer;
import org.sysunit.util.MultiThrowable;

/**
 * A Master Node implementation via JMS which establishes a network of nodes
 * during a specific period of time.
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class MasterNode extends Node {

	private MasterServer server;
	
    public static void main(String[] args)
        throws Throwable {
    	if (args.length <1)  {
    		System.out.println("Usage: <xmlURI> [<slaveTopic>]");
    		return;
    	}
    	
        // lets assume the messenger.xml is on the classpath
        String messengerName = "topicConnection";
        String groupSubject = "SYSUNIT.MASTERS";
        String slaveGroupSubject = "SYSUNIT.SLAVES";
        String xml = args[0];
        if (args.length > 1) {
            slaveGroupSubject = args[1];
        }

        Throwable[] errors = null;

        try {
            Messenger messenger = MessengerManager.get(messengerName);
            if (messenger == null) {
                System.out.println("Could not find a messenger instance called: " + messengerName);
                return;
            }
            Destination groupDestination = messenger.getDestination(groupSubject);
            Destination slaveGroupDestination = messenger.getDestination(slaveGroupSubject);
            MasterServer masterServer = new MasterServer(xml);
            MasterNode controller = new MasterNode(masterServer,
                                                   messenger,
                                                   groupDestination,
                                                   slaveGroupDestination);
            controller.start();

            errors = masterServer.waitFor();

        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }

        if ( errors != null
             &&
             errors.length != 0 ) {
            throw new MultiThrowable( errors );
        }
    }

    public MasterNode(
        MasterServer server,
        Messenger messenger,
        Destination groupDestination,
        Destination slaveGroupDestination)
        throws JMSException {
        super(server, messenger, groupDestination);
        this.server = server;

        server.setSlaveGroupDispatcher(new JmsDispatcher(messenger, slaveGroupDestination, getReplyToDestination()));
    }

}
