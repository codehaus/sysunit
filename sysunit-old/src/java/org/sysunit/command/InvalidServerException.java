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
 * This exception is thrown if the wrong kind of command is sent to the wrong server
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class InvalidServerException extends Exception {

    private Command command;
    private Class expectedType;

    public InvalidServerException(Command command, Class expectedType) {
        super("Could not dispatch command: " + command + " since Server is not of type: " + expectedType.getName());
        this.command = command;
        this.expectedType = expectedType;
    }

    public Command getCommand() {
        return command;
    }

    public Class getExpectedType() {
        return expectedType;
    }

}
