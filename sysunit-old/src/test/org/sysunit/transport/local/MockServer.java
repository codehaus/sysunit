/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit.transport.local;

import org.sysunit.command.Server;

/**
 * A mock server for testing
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class MockServer extends Server {

	private int fooCount;
	
	public void foo() {
		fooCount++;
	}
	
	public int getFooCount() {
		return fooCount;
	}
}
