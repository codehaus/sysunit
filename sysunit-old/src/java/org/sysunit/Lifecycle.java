/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit;

/**
 * <p>A <code>TBean.java</code></b> is a simple lifecycle interface used
 * by services within the SysUnit framework
 * 
 * @author James Strachan
 * @author Bob McWhirter
 * @version $Revision$
 */
public interface Lifecycle {

	public void start() throws Exception;
	
	public void stop() throws Exception;

}
