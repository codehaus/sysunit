/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit.command.slave;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;

public class ProcessRunner
    implements Runnable {

    private static final Log log = LogFactory.getLog(ProcessRunner.class);

    private String executable;
    private String[] arguments;

	/**
	 * A static helper method to create a new instance
	 * 
	 * @param arguments
	 * @return
	 */
	public static ProcessRunner newJavaProcess(Class theClass, String[] arguments) {
		String javaHome = System.getProperty( "java.home" );

		String javaCmd = new File( new File( javaHome,
											 "bin" ),
								   "java" ).getPath();

		// now lets add the Java class to the head of the arguments
		String[] newArgs = new String[arguments.length+1];
		newArgs[0] = theClass.getName();
		System.arraycopy(newArgs, 1, arguments, 0, arguments.length);
		
		return new ProcessRunner( javaCmd, arguments );
	}
	
    public ProcessRunner(String executable,
                         String[] arguments) {

        this.executable = executable;
        this.arguments  = arguments;
    }

    public String getExecutable() {
        return this.executable;
    }

    public String[] getArguments() {
        return this.arguments;
    }

    public String[] getCommandArray() {
        String[] commandArray = new String[ this.arguments.length + 1 ];

        commandArray[ 0 ] = getExecutable();

        String[] arguments = getArguments();

        for ( int i = 0 ; i < arguments.length ; ++i ) {
            commandArray[ i + 1 ] = arguments[ i ];
        }

        return commandArray;
    }

    public void run() {
        Runtime runtime = Runtime.getRuntime();

        try {
            Process process = runtime.exec( getCommandArray() );

            Thread stdoutEater = new Thread( new InputStreamEater( process.getInputStream() ) );
            Thread stderrEater = new Thread( new InputStreamEater( process.getErrorStream() ) );

            stdoutEater.start();
            stderrEater.start();
            
            process.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
