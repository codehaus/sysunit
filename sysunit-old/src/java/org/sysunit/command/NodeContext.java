/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sysunit.jelly.JvmRunner;

/**
 * The Context on which Comamnds execute in a cluster of Nodes
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class NodeContext {
    private static final Log log = LogFactory.getLog(NodeContext.class);

	private JvmRunner runner = new JvmRunner();
	
    public NodeContext() {
    }

    /**
     * @return
     */
    public JvmRunner getRunner() {
        return runner;
    }

    /**
     * @param runner
     */
    public void setRunner(JvmRunner runner) {
        this.runner = runner;
    }

}
