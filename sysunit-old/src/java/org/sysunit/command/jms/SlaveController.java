/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit.command.jms;

import javax.jms.Destination;
import javax.jms.JMSException;

import org.apache.commons.messenger.Messenger;
import org.apache.commons.messenger.MessengerManager;
import org.sysunit.command.Dispatcher;

/**
 * A Slave Node implementation via JMS which waits to be asked to do things
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class SlaveController extends JmsNodeContext {

    private Dispatcher groupDispatcher;
    private Destination replyToDestination;
    private CommandMessageListener messageListener = new CommandMessageListener(this);
    private long waitTime = 2000L;

    public static void main(String[] args) {
        // lets assume the messenger.xml is on the classpath
        String messengerName = "topicConnection";
        String groupTopic = "SYSUNIT.GROUP";
        if (args.length > 0) {
            groupTopic = args[0];
        }

        try {
            Messenger messenger = MessengerManager.get(messengerName);
            if (messenger == null) {
                System.out.println("Could not find a messenger instance called: " + messengerName);
                return;
            }
            Destination destination = messenger.getDestination(groupTopic);
            SlaveController controller = new SlaveController(messenger, destination);
            controller.start();
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public SlaveController(Messenger messenger, Destination destination) throws JMSException {
        super(messenger);
        replyToDestination = messenger.createTemporaryDestination();
        messenger.addListener(replyToDestination, messageListener);
    }

    public void start() throws Exception {
    }

    // Properties
    //-------------------------------------------------------------------------    

    // Implementation methods
    //-------------------------------------------------------------------------    

}
