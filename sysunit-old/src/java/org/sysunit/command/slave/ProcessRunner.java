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
import java.util.Arrays;

public class ProcessRunner
    implements Runnable {

    private static final Log log = LogFactory.getLog(ProcessRunner.class);

    private String executable;
    private String[] arguments;
    private File[] classpath;

	/**
	 * A static helper method to create a new instance
	 * 
	 * @param arguments
	 * @return
	 */
	public static ProcessRunner newJavaProcess(Class theClass,
                                               String[] arguments,
                                               File[] classpath) {

        log.info( "run java proc: " + theClass + Arrays.asList( arguments ) );
		String javaHome = System.getProperty( "java.home" );

		String javaCmd = new File( new File( javaHome,
											 "bin" ),
								   "java" ).getPath();

		// now lets add the Java class to the head of the arguments
		String[] newArgs = new String[arguments.length+1];
		newArgs[0] = theClass.getName();
		System.arraycopy(arguments, 0, newArgs, 1, arguments.length);

		return new ProcessRunner( javaCmd,
                                  newArgs,
                                  classpath );
	}
	
    public ProcessRunner(String executable,
                         String[] arguments,
                         File[] classpath) {

        log.debug( "EXEC: " + executable );
        log.debug( "ARGS: " + Arrays.asList( arguments ) );
        this.executable = executable;
        this.arguments  = arguments;
        this.classpath = classpath;
    }

    public String getExecutable() {
        return this.executable;
    }

    public String[] getArguments() {
        return this.arguments;
    }

    public String[] getCommandArray() {

        String[] commandArray = new String[ this.arguments.length + 3 ];

        commandArray[ 0 ] = getExecutable();

        commandArray[ 1 ] = "-classpath";

        commandArray[ 2 ] = getClasspath();

        String[] arguments = getArguments();

        for ( int i = 0 ; i < arguments.length ; ++i ) {
            commandArray[ i + 3 ] = arguments[ i ];
        }

        return commandArray;
    }

    public String getClasspath() {
        StringBuffer cp = new StringBuffer();

        for ( int i = 0 ; i < this.classpath.length ; ++i ) {
            cp.append( this.classpath[i] );
            if ( i < this.classpath.length ) {
                cp.append( File.pathSeparatorChar );
            }
        }

        return cp.toString();
    }

    public void run() {
        Runtime runtime = Runtime.getRuntime();

        try {
            log.info( "commandArray: " + Arrays.asList( getCommandArray() ) );
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
