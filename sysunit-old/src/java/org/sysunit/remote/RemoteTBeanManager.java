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
import org.sysunit.local.Barrier;
//import org.sysunit.local.LocalSynchronizer;
import org.sysunit.local.TBeanThread;

/**
 * Remote, server side <code>TBeanManager</code> implementation.
 *
 * @see LocalSynchronizer
 *
 * @author <a href="mailto:james.strachan@spiritsoft.com">James Strachan</a>
 * @version $Revision$
 */
public class RemoteTBeanManager implements Runnable {

    // ----------------------------------------------------------------------
    //     Constants
    // ----------------------------------------------------------------------

	/** The Log to which logging calls will be made. */
	private static final Log log = LogFactory.getLog(RemoteTBeanManager.class);
	
    /** Empty <code>String</code> array. */
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    // ----------------------------------------------------------------------
    //     Instace members
    // ----------------------------------------------------------------------

    /** <code>TBean</code>s indexed by <code>String</code> identifier. */
    private Map tbeans;

    /** <code>Thread</code>s for each <code>TBean</code>. */
    private Set tbeanThreads;

    private Synchronizer synchronizer;

    // ----------------------------------------------------------------------
    //     Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct.
     */
    public RemoteTBeanManager(Synchronizer synchronizer) {
        this.tbeans = new HashMap();
        this.tbeanThreads = new HashSet();
        this.synchronizer = synchronizer;
    }

    // ----------------------------------------------------------------------
    //     Instance methods
    // ----------------------------------------------------------------------

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
        if ( tbean instanceof SynchronizableTBean ) {
            ((SynchronizableTBean)tbean).setSynchronizer( new TBeanSynchronizer( tbeanId,
                                                                                 this.synchronizer ) );
        }
	}

	/**
	 * Adds a new TBean, generating the ID automatically
	 * 
	 * @param tbean is the new TBean to be added
	 */
	public void addTBean(TBean tbean) {
		addTBean(tbean.toString(), tbean);
	}

    public void run() {
        try {
        	log.info("About to run tbeans: " + tbeans);
        	
            startTBeans();
            //  waitForTBeans(10000);
            // validateTBeans(case, result);

			log.info("Completed");

        }
        catch (Throwable e) {
            log.error("Caught: " + e, e);
        }
    }

    public void startTBeans() throws Throwable {

        Barrier beginBarrier = new Barrier(this.tbeans.size());
        Barrier endBarrier = new Barrier(this.tbeans.size());

        for (Iterator tbeanIdIter = this.tbeans.keySet().iterator(); tbeanIdIter.hasNext();) {

            String tbeanId = (String) tbeanIdIter.next();
            TBean tbean = (TBean) this.tbeans.get(tbeanId);

            if (tbean instanceof SynchronizableTBean) {
                synchronizer.registerSynchronizableTBean(tbeanId);
            }

            TBeanThread thread = new TBeanThread(tbeanId, tbean, this.synchronizer, beginBarrier, endBarrier);

            this.tbeanThreads.add(thread);

            thread.start();
        }
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
