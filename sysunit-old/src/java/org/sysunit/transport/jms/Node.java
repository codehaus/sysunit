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
import org.sysunit.Lifecycle;
import org.sysunit.command.Server;

/**
 * A base Node implementation
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class Node implements Lifecycle {

	private Server server;
	private Messenger messenger;
    private Destination replyToDestination;
    private Destination groupDestination;
    private CommandMessageListener messageListener;
    
    
    public Node(Server server, Messenger messenger, Destination groupDestination) throws JMSException {
    	this.server = server;
    	this.messenger = messenger;
    	this.groupDestination = groupDestination;
    	
		replyToDestination = messenger.createTemporaryDestination();
		
		JmsAdapter adapter = new JmsAdapter(messenger, replyToDestination);
        messageListener = new CommandMessageListener(adapter, server);
        server.setName(replyToDestination.toString());
		
    }

    public void start() throws Exception {
		// lets subscribe to our own messages
		// and to the group messages
		messenger.addListener(replyToDestination, messageListener);
		messenger.addListener(groupDestination, messageListener);
    }
    
    public void stop() throws Exception {
		messenger.removeListener(replyToDestination, messageListener);
		messenger.removeListener(groupDestination, messageListener);
    }
    
    protected Server getServer() {
    	return server;
    }
    
    protected Destination getReplyToDestination() {
        return replyToDestination;
    }

}
