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
 * The Context on which Comamnds execute in a cluster of Nodes
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class Server implements Lifecycle {
    private static final Log log = LogFactory.getLog(Server.class);

	private String name;
	
    public Server() {
    }

	public void start() throws Exception {
	}

	public void stop() throws Exception {
	}

	/**
	 * 
	 */
	public void shutdown() {
		System.exit(0);
	 }


	// Properties
	//-------------------------------------------------------------------------    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
