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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.sysunit.remote.RemoteTBeanManager;

/**
 * Test case which tests the use of individual TBean instances inside a distributed test case
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class TestJvmRunnerWithTBeans extends TestCase {
	private JvmRunner runner = new JvmRunner();
	
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(TestJvmRunnerWithTBeans.class);
    }

    public void testRunSimpleBean() throws Exception {
        List extent = ExampleTBean.extent();
        extent.clear();

        runJvm("a");

		// lets check the TBeans are instantated correctly
        assertEquals("Number of tbeans created", 2, extent.size());

        ExampleTBean b1 = (ExampleTBean) extent.get(0);
        assertEquals("TBean1: foo", "abc", b1.getFoo());
        assertEquals("TBean1: sleepTime", 100, b1.getSleepTime());

        ExampleTBean b2 = (ExampleTBean) extent.get(1);
        assertEquals("TBean2: foo", "def", b2.getFoo());
        assertEquals("TBean2: sleepTime", 200, b2.getSleepTime());


		// lets check the tbeans are registered with the TBeanManager
		Set tbeans = getTBeanSet();
		
		assertEquals("Size of TBeanManager", 2, tbeans.size());
		assertTrue("TBean1 is in TBeanManager", tbeans.contains(b1));
		assertTrue("TBean1 is in TBeanManager", tbeans.contains(b1));
    }

    public void testRunBeanWithNestedProperty() throws Exception {
        List extent = ExampleTBean.extent();
        extent.clear();

        runJvm("b");
    }

	// Implementation methods
	//-------------------------------------------------------------------------                    

    /**
     * Runs the given named JVM
     * 
     * @param jvmName
     */
    protected void runJvm(String jvmName) throws Exception {
        runner.run("org/sysunit/jelly/tbeanStyleSystemTest.jelly", jvmName);
    }

	/**
	 * @return a Set of all the TBeans in the current RemoteTBeanManager
	 * @throws Exception
	 */
	protected Set getTBeanSet() throws Exception {
		RemoteTBeanManager tbeanManager = runner.getManager();
		
		assertTrue("There must be a RemoteTBeanManager instantiated", tbeanManager != null);
    	
		return new HashSet(tbeanManager.getTBeanMap().values());
	}
}
