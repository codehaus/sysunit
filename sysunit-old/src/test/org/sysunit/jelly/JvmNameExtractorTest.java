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

import java.util.Arrays;
import java.util.List;

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
public class JvmNameExtractorTest extends TestCase {
	protected JvmNameExtractor extractor = new JvmNameExtractor();
	
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(JvmNameExtractorTest.class);
    }

    public void testSomeInvalidXML() throws Exception {
        try {
			List names = extractor.getJvmNamesList("doesNotExist.systest");
            fail("Should have thrown ResourceNotFoundException");
        }
        catch (ResourceNotFoundException e) {
        	// worked
        	assertEquals("Resource", "doesNotExist.systest", e.getResource());
        }
    }

	public void testSampleSystemTestJelly() throws Exception {
		String[] expected = { "a", "a", "a", "a", "b", "b" };
    	
		List names = extractor.getJvmNamesList("org/sysunit/jelly/sampleSystemTest.systest");
    	
		assertListEquals("JVM names returned", expected, names);
	}

	public void testTBeanStyleSystemTestJelly() throws Exception {
		String[] expected = { "a", "a", "a", "a", "b", "b", "b" };
    	
		List names = extractor.getJvmNamesList("org/sysunit/jelly/tbeanStyleSystemTest.systest");
    	
		assertListEquals("JVM names returned", expected, names);
	}

    /**
     * Performs an assertion that the actual List is equivalent to the list of strings
     * 
     * @param message
     * @param expected
     * @param names
     */
    protected void assertListEquals(String message, String[] expected, List names) {
    	assertTrue(message + ": should not be null", names != null);
    	assertEquals(message + ": size of list: " + names, expected.length, names.size());
    	
    	for (int i = 0; i < expected.length; i++ ) {
    		assertEquals(message + ": item(" + i + ")", expected[i], names.get(i));
    	}
    	
    	assertEquals(message + ": lists are equal", Arrays.asList(expected), names);
    }
}
