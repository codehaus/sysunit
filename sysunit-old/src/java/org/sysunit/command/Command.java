/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit.command;

import java.io.Serializable;

/**
 * A Command pattern for inter-server commands
 * 
 * @author James Strachan
 * @version $Revision$
 */
public abstract class Command implements Serializable {
	
	private transient Dispatcher replyDispatcher;
	
	/**
	 * Executes this command on the given server
	 * 
	 * @param server
	 * @throws Exception
	 */
    public abstract void run(Server server) throws Exception;
    
    /**
     * @return the dispatcher which can be used to dispatch replies to this command
     */
    public Dispatcher getReplyDispatcher() {
        return replyDispatcher;
    }

    /**
     * This method allows a local reply dispatcher to be attached to this command.
     * This method should only be called by some remote-Command framework. Such as when
     * a Command has been deserialized and need to be bound to the local reply-dispatcher
     * 
     * @param replyDispatcher
     */
    public void setReplyDispatcher(Dispatcher replyDispatcher) {
        this.replyDispatcher = replyDispatcher;
    }

}