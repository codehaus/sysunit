/* 
 * $Source$ 
 * $Revision$ 
 * $Author$ 
 * $Date$ 
 * 
 * Copyright 1997-2001 by SpiritSoft Ltd, 
 * www.spirit-soft.com 
 * 
 * All rights reserved. 
 * 
 * This software is the confidential and proprietary information 
 * of SpiritSoft Ltd. ("Confidential Information").  You 
 * shall not disclose such Confidential Information and shall use 
 * it only in accordance with the terms of the license agreement 
 * you entered into with SpiritSoft Ltd. 
 */
package org.sysunit.jelly;

import junit.framework.Test;
import junit.textui.TestRunner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sysunit.SystemTestCase;

/**
 * A sample system test case used for testing the Jelly utilities
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class ExampleSystemTest extends SystemTestCase {
    private static final Log log = LogFactory.getLog(ExampleSystemTest.class);
    private static int clientCount;
    private static int serverCount;

    public static void main(String args[]) throws Exception {
        TestRunner.run(suite());
    }

    public static Test suite() throws Exception {
        return suite(ExampleSystemTest.class);
    }

	public static void resetCounts() {
		clientCount = 0;
		serverCount = 0;
	}
	
	public static int getClientCount() {
		return clientCount;
	}

	public static int getServerCount() {
		return serverCount;
	}

	// TBean thread methods
	//-------------------------------------------------------------------------                    
    public void threadClient() throws Exception {
		clientCount++;
        log.info("client thread started: " + clientCount);
    }

    public void threadServer() throws Exception {
		serverCount++;
        log.info("server thread started: " + serverCount);
    }
}
