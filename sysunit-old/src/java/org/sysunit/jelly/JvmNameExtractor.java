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
import java.util.List;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sysunit.remote.RemoteTBeanManager;

/**
 * A helper class which is used by the Master node to extract all the
 * different JVMs which need to be created for a given system test XML document

 * 
 * @author James Strachan
 * @version $Revision$
 */
public class JvmNameExtractor {
    private static final Log log = LogFactory.getLog(JvmNameExtractor.class);
    
    private JellyContext context;
    private RemoteTBeanManager manager;// = new RemoteTBeanManager();

    public JvmNameExtractor() {
        context = new JellyContext();
        context.registerTagLibrary("", new SysUnitTagLibrary());
        manager = new RemoteTBeanManager( null );
    }

    /**
     * Extracts the list of JVM names which are mentioned in the given
     * system test XML document
     * 
     * @param xml
     */
    public List getJvmNames(String xml) throws Exception {
        // lets assume the XML is on the classpath
        URL url = getClass().getClassLoader().getResource(xml);
        if (url == null) {
            url = Thread.currentThread().getContextClassLoader().getResource(xml);
            if (url == null) {
                throw new ResourceNotFoundException(xml);
            }
        }
        XMLOutput output = XMLOutput.createDummyXMLOutput();
        context.runScript(url, output);
        
        // grab the names...
        return (List) context.getVariable("org.sysunit.jvmList");
    }
}
