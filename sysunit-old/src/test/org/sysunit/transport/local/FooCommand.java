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

import junit.framework.AssertionFailedError;

import org.sysunit.command.Command;
import org.sysunit.command.Server;

/**
 * A dummy command for testing
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class FooCommand extends Command {
	
    public void run(Server server) throws Exception {
    	if (server instanceof MockServer) {
    		MockServer mockServer = (MockServer) server;
    		mockServer.foo();
    	}
    	else {
    		throw new AssertionFailedError("Server not an instance of MockServer: " + server);
    	}
    }
}
