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
 * This Command is sent when a new TestNode starts up
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class TestNodeStartedCommand extends MasterCommand {
    private String name;

    public TestNodeStartedCommand(String name) {
		this.name = name;
   }

    public void run(MasterServer context) throws Exception {
    	context.addTestNode(this);
    }
    
    public String getName() {
        return name;
    }
}
