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
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sysunit.TBean;
import org.sysunit.remote.RemoteTBeanManager;

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

    protected void processBean(String var, Object bean) throws JellyTagException {
        super.processBean(var, bean);

        // now lets register the bean with the TBeanManager
        SystemTestTag tag = (SystemTestTag) findAncestorWithClass(SystemTestTag.class);
        if (tag == null) {
            throw new JellyTagException("This tag should be nested inside a <systemTest> tag");
        }
        RemoteTBeanManager manager = tag.getManager();
        manager.addTBean((TBean) bean);
    }

}
