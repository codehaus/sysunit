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

import org.sysunit.command.test.TestServer;

/**
 * @author James Strachan
 * @version $Revision$
 */
public class MockTestServer extends TestServer {
	
	public MockTestServer() {
		super("mockSystemTest.jelly", "mockJvmName");
	}

}
