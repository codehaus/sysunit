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
import org.sysunit.command.InvalidServerException;
import org.sysunit.command.Server;

/**
 * A Command for SlaveServer instances
 * 
 * @author James Strachan
 * @version $Revision$
 */
public abstract class SlaveCommand extends Command {
	
	public void run(Server server) throws Exception {
		if (server instanceof SlaveServer) {
			run((SlaveServer) server);
		}
		else {
			throw new InvalidServerException(this, SlaveServer.class);
		}
	}
	
	/**
	 * Executes this command on the given MasterNode
	 * 
	 * @param context
	 * @throws Exception
	 */
    public abstract void run(SlaveServer context) throws Exception;
}