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
 * A Command object indicating that the node should shudown the JVM
 * that was previously started
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class StopJvmCommand implements Command {
    private static final Log log = LogFactory.getLog(StopJvmCommand.class);

    private String name;

    public StopJvmCommand(String name) {
    	this.name = name;
    }

    public void run(NodeContext context) throws Exception {
    	/** @todo */
    }
}
