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
public class LaunchTestNodeCommand extends SlaveCommand {
    private String xml;
    private String jvmName;
    private String masterID;

    public LaunchTestNodeCommand(String xml, String jvmName, String masterID) {
		this.xml = xml;
		this.jvmName = jvmName;
		this.masterID = masterID;
   }

    public void run(SlaveServer context) throws Exception {
		context.launchTestNode(this);
    }

    public String getXml() {
        return this.xml;
    }

    public String getJvmName() {
        return this.jvmName;
    }
    
    public String getMasterID() {
        return masterID;
    }

}
