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
import org.sysunit.command.slave.SlaveServer;

/**
 * A Slave Node implementation via JMS which waits to be asked to do things
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class SlaveNode extends Node {

    private SlaveServer server;
    private long waitTime = 2000L;

    public static void main(String[] args) {
        // lets assume the messenger.xml is on the classpath
        String messengerName = "topicConnection";
        String groupSubject = "SYSUNIT.SLAVES";
        if (args.length > 0) {
			groupSubject = args[0];
        }

        try {
            Messenger messenger = MessengerManager.get(messengerName);
            if (messenger == null) {
                System.out.println("Could not find a messenger instance called: " + messengerName);
                return;
            }
            Destination groupDestination = messenger.getDestination(groupSubject);
            SlaveNode controller = new SlaveNode(new SlaveServer(), messenger, groupDestination);
            controller.start();
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public SlaveNode(SlaveServer server, Messenger messenger, Destination groupDestination) throws JMSException {
    	super(server, messenger, groupDestination);
    	this.server = server;
    }
}
