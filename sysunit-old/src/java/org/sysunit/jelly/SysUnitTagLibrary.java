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

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.impl.TagFactory;
import org.apache.commons.jelly.impl.TagScript;
import org.xml.sax.Attributes;

/** 
 * Creates a System Test library for defining system tests in XML via Jelly scripts
 *
 * @author <a href="mailto:james.strachan@spiritsoft.com">James Strachan</a>
 * @version $Revision$
 */
public class SysUnitTagLibrary extends TagLibrary {

    public SysUnitTagLibrary() {
        registerTag("systemTest", SystemTestTag.class);
        registerTag("jvm", JvmTag.class);
		registerTag("thread", ThreadTag.class);
		registerTag("tbean", TBeanTag.class);
    }

    // TagLibrary interface
    //-------------------------------------------------------------------------                    
    public TagScript createTagScript(String name, Attributes attributes) throws JellyException {

        // check for standard tags first                        
        TagScript answer = super.createTagScript(name, attributes);
        if (answer != null) {
            return answer;
        }

        // lets try a property tag
        return new TagScript(new TagFactory() {
            public Tag createTag(String name, Attributes attributes) throws JellyException {
                return new TBeanPropertyTag(name);
            }
        });
    }

    // Implementation methods
    //-------------------------------------------------------------------------                    
}
