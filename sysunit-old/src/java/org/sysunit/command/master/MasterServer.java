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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sysunit.command.DispatchException;
import org.sysunit.command.Dispatcher;
import org.sysunit.command.MissingPropertyException;
import org.sysunit.command.Server;
import org.sysunit.command.slave.RequestMembersCommand;
import org.sysunit.command.slave.StartTestNodeCommand;
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
	private long waitTime = 1000L;

    private Dispatcher slaveGroupDispatcher;
	
    public MasterServer() {
    }

	public void start() throws Exception {

		if (slaveGroupDispatcher == null) {
			throw new MissingPropertyException(this, "slaveGroupDispatcher");
		}
		// lets send an advertisement
		slaveGroupDispatcher.dispatch(new RequestMembersCommand());

		// now lets wait until some people arrive...
		Thread.sleep(waitTime);

		// now lets start kicking off the JVMs
		
		String xml = "fooSystemTest.jelly";
		String[] jvms = { "a", "b", "c" };

		roundRobbinJvms(xml, Arrays.asList(jvms));
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

	/**
	 * @return
	 */
	public Dispatcher getSlaveGroupDispatcher() {
		return slaveGroupDispatcher;
	}

	/**
	 * @param slaveGroupDispatcher
	 */
	public void setSlaveGroupDispatcher(Dispatcher slaveGroupDispatcher) {
		this.slaveGroupDispatcher = slaveGroupDispatcher;
	}

	/**
	 * @return
	 */
	public long getWaitTime() {
		return waitTime;
	}

	/**
	 * @param waitTime
	 */
	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}

	// Implementation methods
	//-------------------------------------------------------------------------    
	protected void roundRobbinJvms(String xml, List jvmNames) throws DispatchException {
		// lets create an Array of the dispatchers
		Collection dispatcherCollection = members.values();
		int size = dispatcherCollection.size();
		Dispatcher[] dispatchers = new Dispatcher[size];
		dispatcherCollection.toArray(dispatchers);

		int idx = 0;
		for (Iterator iter = jvmNames.iterator(); iter.hasNext(); idx++) {
			String jvmName = (String) iter.next();

			if (idx >= size) {
				idx = 0;
			}
			dispatchers[idx].dispatch(new StartTestNodeCommand(xml, jvmName));
		}
	}

}
