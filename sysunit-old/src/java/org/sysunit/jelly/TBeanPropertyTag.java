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

import org.apache.commons.jelly.tags.core.UseBeanTag;

/**
 * Creates a nested property via calling a beans createFoo() method then
 * either calling the setFoo(value) or addFoo(value) methods in a similar way
 * to how Ant tags construct themselves.
 * 
 * @author <a href="mailto:james.strachan@spiritsoft.com">James Strachan</a>
 * @version $Revision$
 */
public class TBeanPropertyTag extends UseBeanTag {

	private String createMethodName;
	
     public TBeanPropertyTag(String tagName) {
 
        if (tagName.length() > 0) {
            createMethodName = "create" 
                + tagName.substring(0,1).toUpperCase() 
                + tagName.substring(1);
        }
    }
}
