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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/** 
 * Creates a TBean to add to the current JVM.
 * 
 * @author <a href="mailto:james.strachan@spiritsoft.com">James Strachan</a>
 * @version   $Revision$
 */
public class TBeanTag extends UseBeanTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TBeanTag.class);

	private String name;
	private int count;
	
    public TBeanTag() {
    }
}
