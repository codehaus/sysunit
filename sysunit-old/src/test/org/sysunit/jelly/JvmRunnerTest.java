/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit.jelly;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * A test case of the use of JvmRunner and thread methods
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class JvmRunnerTest extends TestCase {
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(JvmRunnerTest.class);
    }

    public void testSomeInvalidXML() throws Exception {
        try {
        	JvmRunner runner = new JvmRunner();
            runner.run("doesNotExist.systest", "unknownJvmName");
            
            fail("Should have thrown ResourceNotFoundException");
        }
        catch (ResourceNotFoundException e) {
        	// worked
        	assertEquals("Resource", "doesNotExist.systest", e.getResource());
        }
    }

    public void testRunClientJvm() throws Exception {
        ExampleSystemTest.resetCounts();

        runJvm("a");

        assertEquals("Number of clients created", 2, ExampleSystemTest.getClientCount());
        assertEquals("Number of servers created", 0, ExampleSystemTest.getServerCount());
    }

    public void testRunServerJvm() throws Exception {
        ExampleSystemTest.resetCounts();

        runJvm("b");

        assertEquals("Number of clients created", 0, ExampleSystemTest.getClientCount());
        assertEquals("Number of servers created", 1, ExampleSystemTest.getServerCount());
    }

	// Implementation methods
	//-------------------------------------------------------------------------                    

    /**
     * Runs the given named JVM
     * 
     * @param jvmName
     */
    protected void runJvm(String jvmName) throws Exception {
        String[] args = { "org/sysunit/jelly/sampleSystemTest.systest", jvmName };
        JvmRunner.main(args);
        Thread.sleep( 3000 );
    }

}
