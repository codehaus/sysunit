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
import org.sysunit.command.Server;

/**
 * A base Node implementation
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class Node {

	private Server server;
	private Messenger messenger;
    private Destination replyToDestination;
    private Destination groupDestination;
    private CommandMessageListener messageListener;
    
    
    public Node(Server server, Messenger messenger, Destination groupDestination) throws JMSException {
    	this.messenger = messenger;
    	this.groupDestination = groupDestination;
    	
		replyToDestination = messenger.createTemporaryDestination();
		
		JmsAdapter adapter = new JmsAdapter(messenger, replyToDestination);
        messageListener = new CommandMessageListener(adapter, server);
        server.setName(replyToDestination.toString());
		
		// lets subscribe to our own messages
		messenger.addListener(replyToDestination, messageListener);
		
		// and to the group messages
		messenger.addListener(groupDestination, messageListener);
    }

    public void start() throws Exception {
    }
    
    public void stop() throws Exception {
		messenger.addListener(replyToDestination, messageListener);
		
		// and to the group messages
		messenger.addListener(groupDestination, messageListener);
    }
    
    protected Server getServer() {
    	return server;
    }
    
    protected Destination getReplyToDestination() {
        return replyToDestination;
    }

}
