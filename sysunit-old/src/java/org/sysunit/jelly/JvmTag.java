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


/** 
 * Binds a Java bean to the given named Jelly tag so that the attributes of
 * the tag set the bean properties..
 * 
 * @author <a href="mailto:james.strachan@spiritsoft.com">James Strachan</a>
 * @version $Revision$
 */
public class JvmTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(JvmTag.class);

    /** the name of the jvm */
    private String name;
    
    /** the number of JVMs of this configuration to create in the test */
    private int count = 1;
    
    public JvmTag() {
    }
    
    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
        if (name == null) {
            throw new MissingAttributeException("name");
        }
        
		SystemTestTag tag = (SystemTestTag) findAncestorWithClass(SystemTestTag.class);
		if (tag == null) {
			throw new JellyTagException("This tag should be nested inside a <systemTest> tag");
		}
		
        if (isEnabled()) {
            // lets run this JVM locally
            invokeBody(output);
            
            // now lets run the TBeanManager
			//tag.getManager().run();            
        }
        else {
			// lets register the JVM name & count with the system test object
			// in times when a client wishes to remotely create the
			// system test JVMs
            tag.addJvm(name, count);
        }
	}

    
    // Properties
    //-------------------------------------------------------------------------                    

    /** 
     * Sets the name of this JVM
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Sets the number of instances of this JVM to create (defaults to 1)
     * 
     * @param count
     */
    public void setCount(int count) {
        this.count = count;
    }

  
    // Implementation methods
    //-------------------------------------------------------------------------                    
    
    /**
     * @return true if the current JVM should be instantiated.
     */
    protected boolean isEnabled() {
        return name.equals(context.getVariable("org.sysunit.jvm"));
    }
}
