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
import org.sysunit.command.Dispatcher;
import org.sysunit.command.MissingPropertyException;
import org.sysunit.command.Server;
import org.sysunit.command.master.TestNodeStartedCommand;
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
    private Dispatcher masterDispatcher;
    private String jvmName;
    private String xml;

    public TestServer(String xml, String jvmName) {
        this.xml = xml;
        this.jvmName = jvmName;
    }

    public void start() throws Exception {
    	Dispatcher dispatcher = getMasterDispatcher();
    	if (dispatcher == null) {
    		throw new MissingPropertyException(this, "masterDispatcher");
    	}
		dispatcher.dispatch(new TestNodeStartedCommand(getName()));

        // start the test running
        getRunner().run(xml, jvmName);
    }

    // Properties
    //-------------------------------------------------------------------------    

    public JvmRunner getRunner() {
        return runner;
    }

    public void setRunner(JvmRunner runner) {
        this.runner = runner;
    }

    /**
     * @return the Dispatcher used to communicate with the Master server
     */
    public Dispatcher getMasterDispatcher() {
        return masterDispatcher;
    }

    /**
     * @param masterDispatcher
     */
    public void setMasterDispatcher(Dispatcher masterDispatcher) {
        this.masterDispatcher = masterDispatcher;
    }

}
