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
import javax.jms.Message;

import org.apache.commons.messenger.Messenger;
import org.sysunit.command.Command;
import org.sysunit.command.DispatchException;
import org.sysunit.command.Dispatcher;

/**
 * A Dispatcher implementation which uses a remote Queue or Topic
 * to dispatch commands
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class JmsDispatcher implements Dispatcher {
	
	private Messenger messenger;
	private Destination destination;
	private Destination replyToDestination;

	public JmsDispatcher() {
	}

	/**
	 * A Pico style constructor
	 * 
	 * @param messenger
	 * @param destination
	 */
	public JmsDispatcher(Messenger messenger, Destination destination, Destination replyToDestination) {
		setMessenger(messenger);
		setDestination(destination);
		setReplyToDestination(replyToDestination);
	}

    public void dispatch(Command command) throws DispatchException {
    	try {
            Message message = messenger.createObjectMessage(command);
            if (replyToDestination != null) {
            	message.setJMSReplyTo(replyToDestination);
            }
            messenger.send(destination, message);
        }
        catch (JMSException e) {
        	throw new DispatchException(command, e);
        }
    }
    
	// Properties
	//-------------------------------------------------------------------------    

    /**
     * @return
     */
    public Destination getDestination() {
        return destination;
    }

    /**
     * @param destination
     */
    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    /**
     * @return
     */
    public Messenger getMessenger() {
        return messenger;
    }

    /**
     * @param messenger
     */
    public void setMessenger(Messenger messenger) {
        this.messenger = messenger;
    }

    /**
     * @return
     */
    public Destination getReplyToDestination() {
        return replyToDestination;
    }

    /**
     * @param replyToDestination
     */
    public void setReplyToDestination(Destination replyToDestination) {
        this.replyToDestination = replyToDestination;
    }

}
