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

import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.messenger.Messenger;
import org.sysunit.command.Dispatcher;

/**
 * An Adapter class for binding JMS to the Command framework
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class JmsAdapter {
    private static final Log log = LogFactory.getLog(JmsAdapter.class);

	private Map replyDispatchers = new HashMap();
	private Messenger messenger;
	
    public JmsAdapter(Messenger messenger) {
    	this.messenger = messenger;
    }

    /**
     * Looks up and if need be lazily creates a Dispatcher for the given inbound message
     * 
     * @param message
     * @return
     */
    public Dispatcher getReplyDispatcher(Message message) throws JMSException {
		Dispatcher answer = null;
    	Destination replyTo = message.getJMSReplyTo();
    	if (replyTo != null) {
    		answer = (Dispatcher) replyDispatchers.get(replyTo);
    		if (answer == null) {
    			// lets set the reply dispatcher of this dispatcher to the original destination
    			answer = new JmsDispatcher(messenger, replyTo, message.getJMSDestination());
    			replyDispatchers.put(replyTo, answer);
    		}
    	}
        return answer;
    }

}
