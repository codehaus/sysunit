/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit.command.master;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sysunit.command.Server;

/**
 * The Server for Master nodes on the network
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class MasterServer extends Server {
    private static final Log log = LogFactory.getLog(MasterServer.class);

	private Map members = new HashMap();
	private Map testNodes = new HashMap();
	
    public MasterServer() {
    }

	/**
	 * Typically this method is only used by the master to find members
	 * 
	 * @param dispatcher
	 */
	public void acceptMember(AcceptMembershipCommand command) {
		members.put(command.getName(), command.getReplyDispatcher());
	}

	/**
	 * @param command
	 */
	public void addTestNode(TestNodeStartedCommand command) {
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
}
