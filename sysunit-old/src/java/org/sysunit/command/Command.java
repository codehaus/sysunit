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

import java.io.Serializable;

/**
 * A Command pattern for inter-node commands
 * 
 * @author James Strachan
 * @version $Revision$
 */
public interface Command extends Serializable {
	
	/**
	 * Executes this command on the given node context
	 * 
	 * @param context
	 * @throws Exception
	 */
    public void run(NodeContext context) throws Exception;
}