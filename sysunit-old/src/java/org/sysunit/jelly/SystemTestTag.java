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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/** 
 * Creates a System Test library for defining system tests in XML via Jelly scripts
 * 
 * @author <a href="mailto:james.strachan@spiritsoft.com">James Strachan</a>
 * @version $Revision$
 */
public class SystemTestTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(SystemTestTag.class);

    /** The name of this system test */
    private String name;
        
    /** the Java class name to use for the tag */
    private String className;

    /** the ClassLoader used to load beans */
    private ClassLoader classLoader;
    
    /** The system test class */
    private Class systemTestClass;
    
    /** All the JVMs to be created in client mode */
    private List jvms = new ArrayList();
    
    public SystemTestTag() {
    }

    /**
     * Adds a new JVM to this system test
     * 
     * @param name
     * @param count
     */
    public void addJvm(String name, int count) {
        for (int i = 0; i < count; i++ ) {
            jvms.add(name);
        }
    }
    
    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
        if (className == null) {
			throw new MissingAttributeException("className");
		}
        
        jvms.clear();
        systemTestClass = null;
		try {
			ClassLoader classLoader = getClassLoader();
			systemTestClass = classLoader.loadClass(className);
		} 
		catch (ClassNotFoundException e) {
			try {
				systemTestClass = getClass().getClassLoader().loadClass(className);
			} 
            catch (ClassNotFoundException e2) {
				try {
					systemTestClass = Class.forName(className);
				} 
                catch (ClassNotFoundException e3) {
                    log.error( "Could not load class: " + className + " exception: " + e, e );
					throw new JellyTagException(
						"Could not find class: "
							+ className
							+ " using ClassLoader: "
							+ classLoader);
				}
			}
		}
		
        invokeBody(output);
	}

    
    // Properties
    //-------------------------------------------------------------------------                    
    
    /** 
     * Sets the name of the tag to create
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /** 
     * Sets the Java class name to use for the tag
     */
    public void setClassName(String className) {
        this.className = className;
    }
    
    /**
     * Sets the ClassLoader to use to load the class. 
     * If no value is set then the current threads context class
     * loader is used.
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * @return the ClassLoader to use to load classes
     *  or will use the thread context loader if none is specified.
     */    
    public ClassLoader getClassLoader() {
        if ( classLoader == null ) {
            ClassLoader answer = Thread.currentThread().getContextClassLoader();
            if ( answer == null ) {
                answer = getClass().getClassLoader();
            }
            return answer;
        }
        return classLoader;
    }

    // Implementation methods
    //-------------------------------------------------------------------------                    
}
