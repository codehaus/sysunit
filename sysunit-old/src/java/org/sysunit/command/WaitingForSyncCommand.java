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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Command object representing that we're waiting for a given
 * Synchronization point.
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class WaitingForSyncCommand extends Command {
    private static final Log log = LogFactory.getLog(WaitingForSyncCommand.class);

    private String name;

    public WaitingForSyncCommand(String name) {
    	this.name = name;
    }

    public void run(NodeContext context) throws Exception {
    	/** @todo */
    }
}
