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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sysunit.command.DispatchException;
import org.sysunit.command.Dispatcher;
import org.sysunit.command.MissingPropertyException;
import org.sysunit.command.Server;
import org.sysunit.command.slave.RequestMembersCommand;
import org.sysunit.command.slave.LaunchTestNodeCommand;
import org.sysunit.command.test.RunTestCommand;
import org.sysunit.jelly.JvmNameExtractor;
import org.sysunit.SynchronizationException;

/**
 * The Server for Master nodes on the network
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class MasterServer
    extends Server {
    private static final Log log = LogFactory.getLog(MasterServer.class);

	private Map members = new HashMap();
	private Map testNodes = new HashMap();
	private String xml;
	private long waitTime = 1000L;

    private Dispatcher slaveGroupDispatcher;
	private JvmNameExtractor jvmNameExtractor = new JvmNameExtractor();
    private MasterSynchronizer synchronizer = new MasterSynchronizer();

    private List jvmNames;
	
    public MasterServer(String xml) {
    	this.xml = xml;
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

        System.err.println( "###################### " + jvmNames );

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
	public void addTestNode(TestNodeLaunchedCommand command) {
        TestNodeInfo testNodeInfo = new TestNodeInfo( command.getName(),
                                                      command.getNumSynchronizableTBeans(),
                                                      command.getReplyDispatcher() );
        log.info( "adding test node: " + testNodeInfo );
		testNodes.put(command.getName(), testNodeInfo );
        getSynchronizer().addTestNode( testNodeInfo );

        try {
            if ( jvmNames.size() == testNodes.size() ) {
                runTest();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        getSynchronizer().sync( syncCommand.getTestServerName() + ":" + syncCommand.getTBeanId(),
                                syncCommand.getSyncPointName() );
    }

	// Implementation methods
	//-------------------------------------------------------------------------    
	protected void roundRobbinJvms(String xml) throws DispatchException {
		// lets create an Array of the dispatchers
		Collection dispatcherCollection = members.values();
		
		log.info("Dispatching: " + jvmNames.size() + " JVM(s) across: " + dispatcherCollection.size() + " dispatcher(s)");
		
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
			
			dispatcher.dispatch(new LaunchTestNodeCommand(xml, jvmName, mungedName ));
		}
	}

    public void registerSynchronizableTBean(RegisterSynchronizableTBeanCommand command)
        throws DispatchException {
        String beanName = command.getTestServerName() + ":" + command.getTBeanId();
        log.info( "registering: " + beanName );
        this.synchronizer.registerSynchronizableTBean( beanName );
    }

    public void unregisterSynchronizableTBean(UnregisterSynchronizableTBeanCommand command)
        throws DispatchException {
        String beanName = command.getTestServerName() + ":" + command.getTBeanId();
        log.info( "unregistering: " + beanName );
        this.synchronizer.unregisterSynchronizableTBean( beanName );
    }

    public void error(String tbeanId)
        throws DispatchException {
        this.synchronizer.error( tbeanId );
    }
}
