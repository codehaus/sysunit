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

/**
 * This exception is thrown if the given XML resource could not be found
 * @author James Strachan
 * @version $Revision$
 */
public class ResourceNotFoundException extends Exception {

	private String resource;

	public ResourceNotFoundException(String resource) {
		super("Could not find resource: " + resource);
		this.resource = resource;
	}
	
	/**
	 * @return the resource which could not be found
	 */
	public String getResource() {
		return resource;
	}
}
