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
 * A Command object to run a given JVM instance
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class RunJvmCommand implements Command {
    private static final Log log = LogFactory.getLog(RunJvmCommand.class);

    private String xml;
    private String jvmName;

    public RunJvmCommand(String xml, String jvmName) {
		this.xml = xml;
		this.jvmName = jvmName;
   }

    public void run(NodeContext context) throws Exception {
    	context.getRunner().run(xml, jvmName);
    }
}
