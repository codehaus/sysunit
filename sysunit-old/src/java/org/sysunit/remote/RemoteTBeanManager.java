/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit.remote;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import junit.framework.AssertionFailedError;
import junit.framework.TestResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sysunit.SynchronizableTBean;
import org.sysunit.SystemTestCase;
import org.sysunit.TBean;
import org.sysunit.TBeanSynchronizer;
import org.sysunit.Synchronizer;
import org.sysunit.WatchdogException;
import org.sysunit.util.Barrier;
import org.sysunit.util.Blocker;
import org.sysunit.util.Checkpoint;
import org.sysunit.util.CheckpointCallback;
//import org.sysunit.local.LocalSynchronizer;
import org.sysunit.util.TBeanThread;

/**
 * Remote, server side <code>TBeanManager</code> implementation.
 *
 * @see LocalSynchronizer
 *
 * @author <a href="mailto:james.strachan@spiritsoft.com">James Strachan</a>
 * @version $Revision$
 */
public class RemoteTBeanManager { // implements Runnable {

    // ----------------------------------------------------------------------
    //     Constants
    // ----------------------------------------------------------------------

    private static final Throwable[] EMPTY_THROWABLE_ARRAY = new Throwable[0];

	/** The Log to which logging calls will be made. */
	private static final Log log = LogFactory.getLog(RemoteTBeanManager.class);
	
    /** Empty <code>String</code> array. */
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    // ----------------------------------------------------------------------
    //     Instace members
    // ----------------------------------------------------------------------

    private String testServerName;

    /** <code>TBean</code>s indexed by <code>String</code> identifier. */
    private Map tbeans;

    /** <code>Thread</code>s for each <code>TBean</code>. */
    private Set tbeanThreads;

    private Synchronizer synchronizer;

    private CheckpointCallback beginCallback;
    private Blocker beginBlocker;
    private CheckpointCallback endCallback;
    private Blocker endBlocker;
    private CheckpointCallback doneCallback;


    // ----------------------------------------------------------------------
    //     Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct.
     */
    public RemoteTBeanManager(Synchronizer synchronizer,
                              CheckpointCallback beginCallback,
                              CheckpointCallback endCallback,
                              CheckpointCallback doneCallback) {
        this.tbeans = new HashMap();
        this.tbeanThreads = new HashSet();
        this.synchronizer = synchronizer;
        this.beginCallback = beginCallback;
        this.beginBlocker = new Blocker();
        this.endCallback = endCallback;
        this.endBlocker = new Blocker();
        this.doneCallback = doneCallback;
    }

    // ----------------------------------------------------------------------
    //     Instance methods
    // ----------------------------------------------------------------------

    public Blocker getBeginBlocker() {
        return this.beginBlocker;
    }

    public Blocker getEndBlocker() {
        return this.endBlocker;
    }

    public void setTestServerName(String testServerName) {
        this.testServerName = testServerName;
    }

    public String getTestServerName() {
        return this.testServerName;
    }

    /**
     * @see TBeanManager
     */
    public void initialize() {
        // nothing required
    }

	/**
	 * @return the Map of TBeans keyed by their IDs
	 */
	public Map getTBeanMap() {
		return tbeans;
	}

    public int getNumSynchronizableTBeans() {
        int numSynchronizable = 0;

        for ( Iterator tbeanIter = this.tbeans.values().iterator() ;
              tbeanIter.hasNext(); ) {
            if ( tbeanIter.next() instanceof SynchronizableTBean ) {
                ++numSynchronizable;
            }
        }

        return numSynchronizable;
    }
	
	/**
	 * Adds a new TBean to this manager via a key
	 * @param tbeanId the ID of the TBean
	 * @param tbean the TBean to be added
	 */
	public void addTBean(String tbeanId, TBean tbean) {
		tbeans.put(tbeanId, tbean);
	}

	/**
	 * Adds a new TBean, generating the ID automatically
	 * 
	 * @param tbean is the new TBean to be added
	 */
	public void addTBean(TBean tbean) {
		addTBean(tbean.toString(), tbean);
	}

    public void setUpTBeans() {
        try {
        	log.info("About to run tbeans: " + tbeans);
        	
            startTBeans();
            //  waitForTBeans(10000);
            // validateTBeans(case, result);

			log.debug("Completed");

        }
        catch (Throwable e) {
            log.error("Caught: " + e, e);
        }
    }

    public void runTest() {
        getBeginBlocker().unblock();
    }

    public void tearDownTBeans() {
        getEndBlocker().unblock();
    }

    public void startTBeans() throws Throwable {

        log.info( getTestServerName() + " ///// starting TBeans " + this.tbeans.keySet() );

        Checkpoint beginCheckpoint = new Checkpoint( "begin",
                                                     tbeans.size() + 1,
                                                     this.beginCallback );
        
        Checkpoint endCheckpoint = new Checkpoint( "end",
                                                   tbeans.size() + 1,
                                                   this.endCallback );

        Checkpoint doneCheckpoint = new Checkpoint( "done",
                                                    tbeans.size() + 1,
                                                    this.doneCallback );
        
        for (Iterator tbeanIdIter = this.tbeans.keySet().iterator(); tbeanIdIter.hasNext();) {
            
            String tbeanId = (String) tbeanIdIter.next();
            String qualifiedTBeanId = getTestServerName() + ":" + tbeanId;

            TBean tbean = (TBean) this.tbeans.get(tbeanId);

            if ( tbean instanceof SynchronizableTBean ) {
                ((SynchronizableTBean)tbean).setSynchronizer( new TBeanSynchronizer( qualifiedTBeanId,
                                                                                     this.synchronizer ) );
                synchronizer.registerSynchronizableTBean(qualifiedTBeanId);
            }
            
            TBeanThread thread = new TBeanThread( qualifiedTBeanId,
                                                  tbean,
                                                  this.synchronizer,
                                                  beginCheckpoint,
                                                  this.beginBlocker, 
                                                  endCheckpoint,
                                                  this.endBlocker,
                                                  doneCheckpoint );

            this.tbeanThreads.add(thread);

            log.info( this + " starting tbean " + qualifiedTBeanId );
            thread.start();
        }

        doneCheckpoint.pass();
        endCheckpoint.pass();
        beginCheckpoint.pass();
    }

    public void waitForTBeans(long timeout) throws InterruptedException, WatchdogException {

		log.info("Waiting for TBeans...");
		
        long start = new Date().getTime();

        long timeLeft = timeout;

        TBeanThread[] threads = getTBeanThreads();

        for (int i = 0; i < threads.length; ++i) {
            threads[i].join(timeLeft);
            if (timeout > 0) {
                long now = new Date().getTime();
                timeLeft = timeout - (now - start);
            }

            if (timeout > 0 && timeLeft <= 0) {
                ++i;
                Set longTBeanIds = new HashSet();
                for (int j = 0; j < threads.length; ++j) {
                    if (!threads[j].isDone()) {
                        longTBeanIds.add(threads[j].getTBeanId());
                    }
                }

                if (!longTBeanIds.isEmpty()) {
                    throw new WatchdogException(timeout, (String[]) longTBeanIds.toArray(EMPTY_STRING_ARRAY));
                }
                else {
                    break;
                }
            }
        }
    }

    public void validateTBeans(SystemTestCase testCase, TestResult testResult) {

        TBeanThread[] threads = getTBeanThreads();

        for (int i = 0; i < threads.length; ++i) {
            if (threads[i].hasError()) {
                Throwable t = threads[i].getError();

                if (t instanceof AssertionFailedError) {
                    testResult.addFailure(testCase, (AssertionFailedError) t);
                }
                else {
                    testResult.addError(testCase, t);
                }
            }
            else {
                try {
                    threads[i].getTBean().assertValid();
                }
                catch (Throwable t) {
                    if (t instanceof AssertionFailedError) {
                        testResult.addFailure(testCase, (AssertionFailedError) t);
                    }
                    else {
                        testResult.addError(testCase, t);
                    }
                }
            }
        }
    }

    public Throwable[] collectErrors() {

        TBeanThread[] threads = getTBeanThreads();
        List errors = new ArrayList();

        for (int i = 0; i < threads.length; ++i) {
            if (threads[i].hasError()) {
                Throwable t = threads[i].getError();
                errors.add( t );
            } else {
                try {
                    threads[i].getTBean().assertValid();
                }
                catch (Throwable t) {
                    errors.add( t );
                }
            }
        }    

        return (Throwable[]) errors.toArray( EMPTY_THROWABLE_ARRAY );
    }
    
	// ----------------------------------------------------------------------
	//     Implementation methods
	// ----------------------------------------------------------------------
	
	protected TBeanThread[] getTBeanThreads() {
		return (TBeanThread[]) this.tbeanThreads.toArray(TBeanThread.EMPTY_ARRAY);
	}

	protected TBean[] getTBeans() {
		return (TBean[]) this.tbeans.values().toArray(TBean.EMPTY_ARRAY);
	}

	Synchronizer getSynchronizer() {
		return this.synchronizer;
	}
}
