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
 * This exception is thrown of a command could not be dispatched
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class DispatchException extends Exception {

    private Command command;
    private Throwable cause;

    public DispatchException(Command command, Throwable cause) {
        super("Could not dispatch command: " + command + ". Reason: " + cause);
        this.command = command;
        this.cause = cause;
    }

    /**
     * @return the underlying cause of the exception
     */
    public Throwable getCause() {
        return cause;
    }

    /**
     * @return the command which failed
     */
    public Command getCommand() {
        return command;
    }

}
