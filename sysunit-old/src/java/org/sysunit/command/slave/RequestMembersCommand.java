/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit.command.slave;

import org.sysunit.command.Command;
import org.sysunit.command.Server;
import org.sysunit.command.master.AcceptMembershipCommand;


/**
 * A Command sent by the Master to the group to request which members are
 * interested in being part of a test run. This command is typically broadcast
 * or multicast to all members of a network.
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class RequestMembersCommand extends Command {
    public void run(Server context) throws Exception {
    	getReplyDispatcher().dispatch(new AcceptMembershipCommand(context.getName()));
    }
}
