/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit.command.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sysunit.command.Server;
import org.sysunit.jelly.JvmRunner;

/**
 * The Context for TestNodes
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class TestServer extends Server {
    private static final Log log = LogFactory.getLog(TestServer.class);

	private JvmRunner runner = new JvmRunner();
	
    public TestServer() {
    }

	// Properties
	//-------------------------------------------------------------------------    

    public JvmRunner getRunner() {
        return runner;
    }

    public void setRunner(JvmRunner runner) {
        this.runner = runner;
    }
}
