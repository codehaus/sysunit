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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sysunit.SystemTestCase;
import org.sysunit.TBean;
import org.sysunit.ThreadMethodTBeanFactory;
import org.sysunit.remote.RemoteTBeanManager;

/** 
 * Creates a TBean from a named Thread Method
 * 
 * @author <a href="mailto:james.strachan@spiritsoft.com">James Strachan</a>
 * @version $Revision$
 */
public class ThreadTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ThreadTag.class);

    /** the name of the TBean Thread Method */
    private String method;

    /** the number of JVMs of this configuration to create in the test */
    private int count = 1;

    public ThreadTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
        if (method == null) {
            throw new MissingAttributeException("method");
        }

        SystemTestTag systemTestTag = (SystemTestTag) findAncestorWithClass(SystemTestTag.class);

        if (systemTestTag == null) {
            throw new JellyTagException("This tag should be nested inside a <systemTest> tag");
        }

        JvmTag jvmTag = (JvmTag) findAncestorWithClass(JvmTag.class);

        if (jvmTag == null) {
            throw new JellyTagException("This tag should be nested inside a <jvm> tag");
        }
        
        invokeBody(output);

        Class testClass = systemTestTag.getSystemTestClass();
		RemoteTBeanManager manager = systemTestTag.getManager();
        try {
            for (int i = 0; i < count; i++) {
                SystemTestCase instance = (SystemTestCase) testClass.newInstance();
                ThreadMethodTBeanFactory factory = new ThreadMethodTBeanFactory(instance, method);
                TBean tbean = factory.newTBean();
                String tbeanId = jvmTag.getName() + ":" + method + ":" + (i+1);
                manager.addTBean(tbeanId, tbean);
            }
        }
        catch (Exception e) {
            throw new JellyTagException("Could not instantiate: " + testClass + ": Reason: " + e, e);
        }
    }

    // Properties
    //-------------------------------------------------------------------------                    
    /**
     * @return
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the number of concurrent instances of this thread to create
     * @param count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return
     */
    public String getMethod() {
        return method;
    }

    /**
     * The name of the method to run in a separate thread as part of the test
     * @param method
     */
    public void setMethod(String method) {
        this.method = method;
    }

}
