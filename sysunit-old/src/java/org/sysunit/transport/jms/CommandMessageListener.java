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

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sysunit.command.Command;
import org.sysunit.command.Server;

/**
 * A JMS MessageListener which will consume Command objects and execute them against
 * a given context
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class CommandMessageListener implements MessageListener {

    private static final Log log = LogFactory.getLog(CommandMessageListener.class);

    private JmsAdapter adapter;
	private Server server;

	public CommandMessageListener(JmsAdapter adapter, Server server) {
		this.adapter = adapter;
		this.server = server;
	}
	
    public void onMessage(Message message) {
        log.debug( this.server.getName() + " onMessage(" + message + ")" );
        if (message instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                Object value = objectMessage.getObject();
                if (value instanceof Command) {
                    onCommand(message, (Command) value);
                }
                else {
                    log.error("Received object which was not a command: " + value);
                }
            }
            catch (Exception e) {
                log.error("Could not process message: " + message + ". Reason: " + e, e);
            }
        }
        else {
            log.error("Uknown message type: " + message);
        }
    }

    // Implementation methods
    //-------------------------------------------------------------------------    

    /**
     * @param command
     */
    protected void onCommand(Message message, Command command) throws Exception {
    	command.setReplyDispatcher(adapter.getReplyDispatcher(message));
    	command.run(server);
    }

}
