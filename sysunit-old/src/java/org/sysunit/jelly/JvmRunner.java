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
import org.sysunit.Synchronizer;
import org.sysunit.SynchronizationException;
import org.sysunit.local.LocalSynchronizer;
import org.sysunit.command.test.TestSynchronizer;
import org.sysunit.remote.RemoteTBeanManager;

/**
 * A helper class which will execute the named JVM inside the system test XML document

 * 
 * @author James Strachan
 * @version $Revision$
 */
public class JvmRunner {
    private static final Log log = LogFactory.getLog(JvmRunner.class);
    
    private JellyContext context;
    private RemoteTBeanManager manager;
    private Synchronizer synchronizer;

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: JvmRunner <systemTestXml> <jvmName>");
        }
        try {
            String xml = args[0];
            String jvmName = args[1];
            JvmRunner runner = new JvmRunner();
            runner.run(xml, jvmName);
            runner.getManager().run();
        }
        catch (Exception e) {
            log.error("Caught: " + e, e);
            throw e;
        }
    }

    public JvmRunner() {
        //this.synchronizer = new TestSynchronizer();
        this( new LocalSynchronizer() );
    }

    public JvmRunner(Synchronizer synchronizer) {
        this.context = new JellyContext();
        this.context.registerTagLibrary("", new SysUnitTagLibrary());
        this.synchronizer = synchronizer;
        this.manager = new RemoteTBeanManager( this.synchronizer );
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
        context.setVariable("org.sysunit.TBeanManager", getManager());
        context.setVariable("org.sysunit.Synchronizer", getSynchronizer());

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
    }

    // Properties
    //-------------------------------------------------------------------------    

    /**
     * @return
     */
    public RemoteTBeanManager getManager() {
        return manager;
    }

    /**
     * @param manager
     */
    public void setManager(RemoteTBeanManager manager) {
        this.manager = manager;
    }

    public Synchronizer getSynchronizer() {
        return this.synchronizer;
    }

}
