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


/**
 * A dispatcher of commands. Typically this would make remote messaging
 * calls such as JMS, JavaGroups or even HTTP requests.
 * 
 * @author James Strachan
 * @version $Revision$
 */
public interface Dispatcher {
	
	public void dispatch(Command command) throws DispatchException;
}
