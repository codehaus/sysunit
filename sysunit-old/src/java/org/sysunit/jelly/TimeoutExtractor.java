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

public class TimeoutExtractor {
    private static final Log log = LogFactory.getLog(TimeoutExtractor.class);
    
    private JellyContext context;
    private RemoteTBeanManager manager;

    public TimeoutExtractor() {
        context = new JellyContext();
        context.registerTagLibrary("", new SysUnitTagLibrary());
        manager = new RemoteTBeanManager( null,
                                          null,
                                          null,
                                          null );
    }

    public long getTimeout(String xml) throws Exception {
        URL url = getClass().getClassLoader().getResource(xml);
        if (url == null) {
            url = Thread.currentThread().getContextClassLoader().getResource(xml);
            if (url == null) {
                url = ClassLoader.getSystemClassLoader().getSystemResource(xml);
                if (url == null) {
                    throw new ResourceNotFoundException(xml);
                }
            }
        }

        XMLOutput output = XMLOutput.createDummyXMLOutput();
        context.runScript(url, output);
        
        // grab the names...
        return ((Long)context.getVariable("org.sysunit.timeout")).longValue();
    }
}
