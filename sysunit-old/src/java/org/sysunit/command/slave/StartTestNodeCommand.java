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



/**
 * This Command starts a new TestNode JVM, either as an entirely new fork
 * or in a separate new ClassLoader
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class StartTestNodeCommand extends SlaveCommand {
    private String xml;
    private String jvmName;

    public StartTestNodeCommand(String xml, String jvmName) {
		this.xml = xml;
		this.jvmName = jvmName;
   }

    public void run(SlaveServer context) throws Exception {
		context.startTestNode(this);
    }
}
