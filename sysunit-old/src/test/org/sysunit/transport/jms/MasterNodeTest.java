/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit.transport.jms;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * A test case 
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class MasterNodeTest extends TestCase {
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(MasterNodeTest.class);
    }

    public void testMasterAndSlave() throws Exception {
		String[] args = {};
		SlaveNode.main(args);
		
		// we should wait a moment until the slave starts up
		Thread.sleep(1000);	
		    
    	MasterNode.main(args);
    }

 
	// Implementation methods
	//-------------------------------------------------------------------------                    

 
}
