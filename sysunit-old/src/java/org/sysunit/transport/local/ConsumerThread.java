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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sysunit.command.Command;
import org.sysunit.command.Server;

/**
 * A thread which removes Command objects from the queue and runs them
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class ConsumerThread extends Thread {
    private static final Log log = LogFactory.getLog(ConsumerThread.class);

	private Server server;
	private MTQueue queue;
    private boolean running = false;

    public ConsumerThread(Server server, MTQueue queue, String id) {
        super(id);
        this.server = server;
        this.queue = queue;
    }

    public void start() {
        running = true;
        super.start();
    }

    public void stopRunning() {
        running = false;
    }

    public void run() {
        while (running) {
            try {
                // in case a wait() gets lost by a bad JVM
                // lets not block forever but just for 10 seconds at a time
                Command command = (Command) queue.remove(10000);
                if (command != null) {
                    command.run(server);
                }
            }
            catch (Throwable e) {
                log.warn("caught an Exception in run(): " + e, e);
            }
        }
    }
}