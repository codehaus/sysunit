/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit.command.master;

import org.sysunit.command.StateServer;
import org.sysunit.command.Dispatcher;
import org.sysunit.command.MissingPropertyException;
import org.sysunit.jelly.JvmNameExtractor;
import org.sysunit.jelly.TimeoutExtractor;

import java.io.File;
import java.io.FileInputStream;

public class MasterServer
    extends StateServer {

    private String xml;
    private String[] jvmNames;
    private Dispatcher[] slaveNodeDispatchers;

    private boolean isDone;
    private Object isDoneLock;
    private Throwable[] errors;

    private long timeout;

    public MasterServer(String xml) {
        this.xml = xml;
        this.isDone = false;
        this.isDoneLock = new Object();
        this.timeout = 0;
    }

    // ----------------------------------------------------------------------
    // ----------------------------------------------------------------------

    public String getXml() {
        return this.xml;
    }
    
    public String[] getJvmNames() {
        return this.jvmNames;
    }

    public void setSlaveNodeDispatchers(Dispatcher[] slaveNodeDispatchers) {
        this.slaveNodeDispatchers = slaveNodeDispatchers;
    }

    public Dispatcher[] getSlaveNodeDispatchers() {
        return this.slaveNodeDispatchers;
    }

    // ----------------------------------------------------------------------
    // ----------------------------------------------------------------------

	public void start() throws Exception {

		if ( getXml() == null ) {
			throw new MissingPropertyException( this,
                                                "xml" );
		}
		
        JvmNameExtractor jvmNameExtractor = new JvmNameExtractor();
		this.jvmNames = jvmNameExtractor.getJvmNames( xml );

        TimeoutExtractor timeoutExtractor = new TimeoutExtractor();
        this.timeout = timeoutExtractor.getTimeout( xml );

        enterState( new LaunchState( this,
                                     getSlaveNodeDispatchers(),
                                     getXml(),
                                     getJvmNames() ) );
    }

    // ----------------------------------------------------------------------
    // ----------------------------------------------------------------------


    protected synchronized void exitState(LaunchState state)
        throws Exception {
        if ( getState() != state ) {
            return;
        }
        enterState( new SetUpState( this,
                                    state.getTestNodeInfos() ) );
    }

    protected synchronized void exitState(SetUpState state)
        throws Exception {
        if ( getState() != state ) {
            return;
        }
        enterState( new RunState( this,
                                  state.getTestNodeInfos(),
                                  timeout ) );
    }

    protected synchronized void exitState(RunState state)
        throws Exception {
        if ( getState() != state ) {
            return;
        }
        enterState( new TearDownState( this,
                                       state.getTestNodeInfos() ) );
    }

    protected synchronized void exitState(TearDownState state)
        throws Exception {
        if ( getState() != state ) {
            return;
        }

        this.errors = state.getErrors();

        synchronized ( this.isDoneLock ) {
            this.isDone = true;
            this.isDoneLock.notifyAll();
        }
    }

    public Throwable[] waitFor()
        throws Exception {
        synchronized ( this.isDoneLock ) {
            while ( ! this.isDone ) {
                this.isDoneLock.wait( 1000 );
            }
        }

        return this.errors;
    }
    
    /*
		getSlaveGroupDispatcher().dispatch( new RequestMembersCommand() );

		Thread.sleep(waitTime);


        TimeoutExtractor timeoutExtractor = new TimeoutExtractor();
        this.timeout  = timeoutExtractor.getTimeout(xml);

        setState( new LaunchState() );

        if ( this.members.isEmpty() ) {
            throw new SysUnitException( "No slave JVMs" );
        }

		roundRobbinJvms(xml);
	}
    */

    // ----------------------------------------------------------------------
    // ----------------------------------------------------------------------

    public byte[] requestJar(String jarName,
                             String path)
        throws Exception {

        File file = new File( path );

        FileInputStream in = new FileInputStream( file );

        try {
            byte bytes[] = new byte[ (int) file.length() ];
            
            in.read( bytes );
            
            return bytes;
        } finally {
            in.close();
        }
    }


}
