/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit.mock;

import org.sysunit.command.master.MasterServer;

/**
 * @author James Strachan
 * @version $Revision$
 */
public class MockMasterServer extends MasterServer {
	
	public MockMasterServer() {
		super("dummySysTest.jelly");
	}
}
