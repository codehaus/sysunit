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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sysunit.jelly.JvmRunner;

/**
 * The Context on which Comamnds execute in a cluster of Nodes
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class NodeContext {
    private static final Log log = LogFactory.getLog(NodeContext.class);

	private JvmRunner runner = new JvmRunner();
	private Map members = new HashMap();
	private String name;
	
    public NodeContext() {
    }

	/**
	 * Typically this method is only used by the master to find members
	 * 
	 * @param dispatcher
	 */
	public void acceptMember(AcceptMembershipCommand command) {
		members.put(command.getName(), command.getReplyDispatcher());
	}

	// Properties
	//-------------------------------------------------------------------------    

	/**
	 * @return a Map keyed by name of all the members's Dispatcher object
	 */
	public Map getMemberMap() {
		return members;
	}
	
    public JvmRunner getRunner() {
        return runner;
    }

    public void setRunner(JvmRunner runner) {
        this.runner = runner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
