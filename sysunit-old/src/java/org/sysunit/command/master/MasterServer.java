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

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sysunit.command.DispatchException;
import org.sysunit.command.Dispatcher;
import org.sysunit.command.MissingPropertyException;
import org.sysunit.command.Server;
import org.sysunit.command.slave.RequestMembersCommand;
import org.sysunit.command.slave.LaunchTestNodeCommand;
import org.sysunit.command.test.SetUpTBeansCommand;
import org.sysunit.command.test.TearDownTBeansCommand;
import org.sysunit.command.test.RunTestCommand;
import org.sysunit.jelly.JvmNameExtractor;
import org.sysunit.jelly.TimeoutExtractor;
import org.sysunit.SysUnitException;
import org.sysunit.SynchronizationException;
import org.sysunit.WatchdogException;

/**
 * The Server for Master nodes on the network
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class MasterServer
    extends Server {
    private static final Throwable[] EMPTY_THROWABLE_ARRAY = new Throwable[0];

    private static final Log log = LogFactory.getLog(MasterServer.class);

	private Map members = new HashMap();
	private Map testNodes = new HashMap();
	private String xml;
	private long waitTime = 1000L;
    private long timeout;

    private Dispatcher slaveGroupDispatcher;
	private JvmNameExtractor jvmNameExtractor = new JvmNameExtractor();
	private TimeoutExtractor timeoutExtractor = new TimeoutExtractor();
    private MasterSynchronizer synchronizer = new MasterSynchronizer();

    private Object isDoneLock = new Object();
    private boolean isDone;

    private List jvmNames;
    private int setUpServers;
    private int ranServers;
    private int doneServers;

    private List errors;
	
    public MasterServer(String xml) {
    	this.xml = xml;
        this.errors = new ArrayList();
    }

	public void start() throws Exception {
		if (xml == null) {
			throw new MissingPropertyException(this, "xml");
		}
		if (slaveGroupDispatcher == null) {
			throw new MissingPropertyException(this, "slaveGroupDispatcher");
		}
		
		// lets send an advertisement
		slaveGroupDispatcher.dispatch(new RequestMembersCommand());

		// now lets wait until some people arrive...
		Thread.sleep(waitTime);

		// now lets start kicking off the JVMs
		this.jvmNames = jvmNameExtractor.getJvmNames(xml);
        this.timeout = timeoutExtractor.getTimeout(xml);

        System.err.println( "###################### " + jvmNames );

        if ( this.members.isEmpty() ) {
            throw new SysUnitException( "No slave JVMs" );
        }

		roundRobbinJvms(xml);
	}
	
	
	/**
	 * Typically this method is only used by the master to find members
	 * 
	 * @param dispatcher
	 */
	public void acceptMember(AcceptMembershipCommand command) {
		members.put(command.getName(), command.getReplyDispatcher());
	}

	/**
	 * @param command
	 */
	public void addTestNode(TestNodeLaunchedCommand command)
        throws Exception {
        TestNodeInfo testNodeInfo = new TestNodeInfo( command.getName(),
                                                      command.getNumSynchronizableTBeans(),
                                                      command.getReplyDispatcher() );
        log.info( "adding test node: " + testNodeInfo );
		testNodes.put(command.getName(), testNodeInfo );
        getSynchronizer().addTestNode( testNodeInfo );

        if ( jvmNames.size() == testNodes.size() ) {
            setUpTBeans();
        }
	}

    protected void setUpTBeans()
        throws Exception {
        for ( Iterator testNodeInfoIter = this.testNodes.values().iterator();
              testNodeInfoIter.hasNext(); ) {
            TestNodeInfo testNodeInfo = (TestNodeInfo) testNodeInfoIter.next();

            log.info( "starting test on " + testNodeInfo.getName() );
            testNodeInfo.getDispatcher().dispatch( new SetUpTBeansCommand() );
            log.info( "started test on " + testNodeInfo.getName() );
        }
    }
    protected void tearDownTBeans()
        throws Exception {
        for ( Iterator testNodeInfoIter = this.testNodes.values().iterator();
              testNodeInfoIter.hasNext(); ) {
            TestNodeInfo testNodeInfo = (TestNodeInfo) testNodeInfoIter.next();

            testNodeInfo.getDispatcher().dispatch( new TearDownTBeansCommand() );
        }
    }

    public void tbeansSetUp(String testServerName)
        throws Exception {
        ++this.setUpServers;

        if ( this.setUpServers == jvmNames.size() ) {
            runTest();
        }
    }

    public void tbeansRan(String testServerName)
        throws Exception {
        ++this.ranServers;
        
        if ( this.ranServers == jvmNames.size() ) {
            tearDownTBeans();
        }
    }
    
    public void tbeansDone(String testServerName,
                           Throwable[] errors) {
        addErrors( errors );
        
        ++this.doneServers;
        
        if ( this.doneServers == jvmNames.size() ) {
            if ( ! this.errors.isEmpty() ) {
                log.error( "THERE WERE ERRORS: " + this.errors );
            } else {
                log.info( "SUCCESSFUL" );
            }
            
            synchronized ( this.isDoneLock ) {
                this.isDone = true;
                this.isDoneLock.notifyAll();
            }
        }
    }

    public Throwable[] waitFor()
        throws Exception {

        long start = new Date().getTime();

        long timeLeft = timeout;

        log.info( "waitForing: " + timeLeft );
        synchronized ( this.isDoneLock ) {
            while ( ! this.isDone ) {
                log.info( "this.isDone == " + this.isDone + " timeLeft: " + timeLeft );
                this.isDoneLock.wait( timeLeft );

                if ( timeout > 0 ) {
                    long now = new Date().getTime();
                    timeLeft = timeout - ( now - start );
                }

                if ( timeout > 0
                     &&
                     timeLeft <= 0 ) {
                    
                    killAll();
                    throw new WatchdogException( timeout,
                                                 new String[0] );
                }
            }
        }

        return (Throwable[]) this.errors.toArray( EMPTY_THROWABLE_ARRAY );
    }

    public void killAll()
        throws DispatchException {

    }

    public void addErrors(Throwable[] errors) {
        for ( int i = 0 ; i < errors.length ; ++i ) {
            this.errors.add( errors[i] );
        }
    }
        

    protected void runTest()
        throws Exception {

        for ( Iterator testNodeInfoIter = this.testNodes.values().iterator();
              testNodeInfoIter.hasNext(); ) {
            TestNodeInfo testNodeInfo = (TestNodeInfo) testNodeInfoIter.next();

            log.info( "starting test on " + testNodeInfo.getName() );
            testNodeInfo.getDispatcher().dispatch( new RunTestCommand() );
            log.info( "started test on " + testNodeInfo.getName() );
        }
    }

    public TestNodeInfo getTestNodeInfo(String name) {
        return (TestNodeInfo) testNodes.get( name );
    }

	// Properties
	//-------------------------------------------------------------------------    

	/**
	 * @return a Map keyed by name of all the members's Dispatcher object
	 */
	public Map getMemberMap() {
		return members;
	}

    public Map getTestNodesMap() {
        return testNodes;
    }

	/**
	 * @return
	 */
	public Dispatcher getSlaveGroupDispatcher() {
		return slaveGroupDispatcher;
	}

	/**
	 * @param slaveGroupDispatcher
	 */
	public void setSlaveGroupDispatcher(Dispatcher slaveGroupDispatcher) {
		this.slaveGroupDispatcher = slaveGroupDispatcher;
	}

	/**
	 * @return
	 */
	public long getWaitTime() {
		return waitTime;
	}

	/**
	 * @param waitTime
	 */
	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}
	/**
	 * @return
	 */
	public String getXml() {
		return xml;
	}

	/**
	 * @param xml
	 */
	public void setXml(String xml) {
		this.xml = xml;
	}

    public MasterSynchronizer getSynchronizer() {
        return this.synchronizer;
    }

    public void sync(SyncCommand syncCommand)
        throws SynchronizationException, DispatchException {
        getSynchronizer().sync( syncCommand.getTBeanId(),
                                syncCommand.getSyncPointName() );
    }

	// Implementation methods
	//-------------------------------------------------------------------------    
	protected void roundRobbinJvms(String xml) throws Exception {
		// lets create an Array of the dispatchers
		Collection dispatcherCollection = members.values();
		
		log.info("Dispatching: " + jvmNames.size() + " JVM(s) across: " + dispatcherCollection.size() + " dispatcher(s)");

        Properties jarMap = new Properties();

        InputStream jarsIn = getClass().getClassLoader().getResourceAsStream( "sysunit-jars.properties" );
        
        if ( jarsIn != null ) {
            try {
                jarMap.load( jarsIn );
            } finally {
                jarsIn.close();
            }
        }

        log.info( jarMap );

        //if ( 1 == 1 ) {
            //throw new Error( "foo" );
        //}
		
		int size = dispatcherCollection.size();
		Dispatcher[] dispatchers = new Dispatcher[size];
		dispatcherCollection.toArray(dispatchers);

		int idx = 0;
		for (Iterator iter = jvmNames.iterator(); iter.hasNext(); idx++) {
			String jvmName = (String) iter.next();

			if (idx >= size) {
				idx = 0;
			}
			
			Dispatcher dispatcher = dispatchers[idx];
			
			log.info("Dispatching jvm: " + jvmName + " to dispatcher: " + dispatcher);

            String mungedName = getName().substring( 0,
                                                     getName().lastIndexOf( "-false" ) );

            // String mungedName = getName();
			
			dispatcher.dispatch(new LaunchTestNodeCommand(xml,
                                                          jvmName,
                                                          mungedName,
                                                          jarMap));
		}
	}

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

    public void registerSynchronizableTBean(RegisterSynchronizableTBeanCommand command)
        throws DispatchException {
        this.synchronizer.registerSynchronizableTBean( command.getTBeanId() );
    }

    public void unregisterSynchronizableTBean(UnregisterSynchronizableTBeanCommand command)
        throws DispatchException {
        this.synchronizer.unregisterSynchronizableTBean( command.getTBeanId() );
    }

    public void error(String tbeanId)
        throws DispatchException {
        this.synchronizer.error( tbeanId );
    }
}
