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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.sysunit.command.Dispatcher;

/**
 * A test case 
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class TestLocalDispatcher extends TestCase {

	protected MockServer server = new MockServer();
	
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(TestLocalDispatcher.class);
    }

    public void testSimpleDispatch() throws Exception {
    	
    	Dispatcher dispatcher = createDispatcher();
    	
    	dispatcher.start();
    	
    	dispatcher.dispatch(new FooCommand());
    	
    	// wait a moment for async stuff
        Thread.sleep(1000);
        
        assertEquals("Should have invoked foo by now", 1, server.getFooCount());
        
        dispatcher.stop();
    }

    /**
     * Factory method to create a new dispatcher
     * @return a new dispatcher of commands to the given server
     */
    protected Dispatcher createDispatcher() {
         return new LocalDispatcher(server);
    }
}
