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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.messenger.Messenger;
import org.sysunit.command.Lifecycle;
import org.sysunit.command.Server;

/**
 * A base Node implementation
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class Node implements Lifecycle {

	private static final Log log = LogFactory.getLog(Node.class);

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
		log.debug("Subscribing to personal commands for me on: " + replyToDestination);
		messenger.addListener(replyToDestination, messageListener);

		log.debug("Subscribing to group commands on: " + groupDestination);
		messenger.addListener(groupDestination, messageListener);

		server.start();
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
