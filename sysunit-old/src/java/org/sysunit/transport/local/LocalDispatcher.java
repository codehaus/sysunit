/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit.transport.local;

import org.sysunit.command.Command;
import org.sysunit.command.DispatchException;
import org.sysunit.command.Dispatcher;
import org.sysunit.command.Server;

/**
 * A Dispatcher implementation which uses a simple queue and separate threads
 * to handle the dispatch of Command objects
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class LocalDispatcher implements Dispatcher {
	
	private MTQueue queue;
	private ConsumerThread thread;

	public LocalDispatcher(Server server) {
		this.queue = new MTQueue();
		this.thread = new ConsumerThread(server, queue, "ConsumerThread for: " + toString());
	}

    public void dispatch(Command command) throws DispatchException {
    	queue.add(command);
    }
    
    public void start() {
    	thread.start();
    }
    
    public void stop() {
    	thread.stopRunning();
    }
}
