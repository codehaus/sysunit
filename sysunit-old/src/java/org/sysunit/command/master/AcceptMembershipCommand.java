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

/**
 * A reply to the {@link RequestMembersCommand} to indicate that 
 * this node would like to join the test network.
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class AcceptMembershipCommand
    extends DiscoveryState.Command {

	private String name;
	
	public AcceptMembershipCommand(String name) {
		this.name = name;
	}
    
    public void run(DiscoveryState state)
        throws Exception {
    	state.addSlaveNode( getName(),
                            getReplyDispatcher() );
    }
    
    public String getName() {
        return name;
    }

}
