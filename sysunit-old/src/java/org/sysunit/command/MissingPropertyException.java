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
 * This exception is thrown when a Server is tried to be started and
 * some missing dependency is not specified.
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class MissingPropertyException extends Exception {

    private Object source;
    private String property;

    public MissingPropertyException(Object source, String property) {
        super("Missing property on: " + source + " due to missing property: " + property);
		this.source = source;
		this.property = property;
    }

    public String getProperty() {
        return property;
    }

    public Object getSource() {
        return source;
    }

}
