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

import java.net.URL;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A helper class which will create the given JVM name of the specified 
 * systemTest XML document.
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class JvmRunner {
	private static final Log log = LogFactory.getLog(JvmRunner.class);
    private JellyContext context;

    public static void main(String[] args) throws Exception {
    	if (args.length < 2) {
    		System.out.println("Usage: JvmRunner <systemTestXml> <jvmName>");
    	}
    	try {
            String xml = args[0];
            String jvmName = args[1];
            JvmRunner runner = new JvmRunner();
            runner.run(xml, jvmName);
        }
        catch (Exception e) {
        	log.error("Caught: " + e, e);
        	throw e;
        }
    }

	public JvmRunner() {
		context = new JellyContext();
		context.registerTagLibrary("", new SysUnitTagLibrary());
	}
	
    /**
     * Runs the given JVM named in the XML document using the classloader
     * to load the resource
     * 
     * @param xml
     * @param jvmName
     */
    public void run(String xml, String jvmName) throws Exception {
    	context.setVariable("org.sysunit.jvm", jvmName);
    	
    	// lets assume the XML is on the classpath
    	URL url = getClass().getClassLoader().getResource(xml);
    	if (url == null) {
    		throw new ResourceNotFoundException(xml);
    	}
        XMLOutput output = XMLOutput.createDummyXMLOutput();
    	context.runScript(url, output);
    }
}
