package org.sysunit.local;

/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The SpiritSoft Software License, Version 1.0
 *
 * Copyright (c) 2003 SpiritSoft, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        SpiritSoft, Inc (http://www.spiritsoft.com/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "SysUnit" and "SpiritSoft" must not be used to endorse 
 *    or promote products derived from this software without prior 
 *    written permission. 
 *    For written permission, please contact info@spiritsoft.com.
 *
 * 5. Products derived from this software may not be called "SysUnit"
 *    nor may "SysUnit" appear in their names without prior written
 *    permission of the SpiritSoft, Inc.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL SPIRITSOFT, INC. OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the SpiritSoft, Inc.  For more
 * information on the SpiritSoft, Inc, please see
 * <http://www.spiritsoft.com/>.
 *
 */

import org.sysunit.TBean;
import org.sysunit.TBeanManager;
import org.sysunit.SynchronizableTBean;
import org.sysunit.TBeanSynchronizer;
import org.sysunit.SystemTestCase;
import org.sysunit.SynchronizationException;
import org.sysunit.TBeanThrowable;
import org.sysunit.WatchdogException;

import junit.framework.TestResult;
import junit.framework.AssertionFailedError;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;

/**
 * Single-JVM <code>TBeanManager</code> implementation.
 *
 * @see LocalSynchronizer
 *
 * @author Bob McWhirter
 *
 * @version $Id$
 */
public class LocalTBeanManager
    implements TBeanManager {

    // ----------------------------------------------------------------------
    //     Constants
    // ----------------------------------------------------------------------

    private static final Throwable[] EMPTY_THROWABLE_ARRAY = new Throwable[0];

    // ----------------------------------------------------------------------
    //     Instace members
    // ----------------------------------------------------------------------

    /** <code>TBean</code>s indexed by <code>String</code> identifier. */
    private Map tbeans;

    /** <code>Thread</code>s for each <code>TBean</code>. */
    private Set tbeanThreads;

    // ----------------------------------------------------------------------
    //     Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct.
     */
    public LocalTBeanManager() {
        this.tbeans       = new HashMap();
        this.tbeanThreads = new HashSet();
    }

    // ----------------------------------------------------------------------
    //     Instance methods
    // ----------------------------------------------------------------------

    /**
     * @see TBeanManager
     */
    public void init() {
        // nothing required
    }

    /**
     * @see TBeanManager
     */
    public void startTBeans(SystemTestCase testCase,
                            TestResult testResult)
        throws Throwable {

        String[] factoryNames = testCase.getTBeanFactoryNames();
        
        final LocalSynchronizer synchronizer = new LocalSynchronizer();

        for ( int i = 0 ; i < factoryNames.length ; ++i ) {
            final String tbeanId = factoryNames[i];
            TBean tbean = testCase.getTBeanFactory( tbeanId ).newTBean();

            this.tbeans.put( tbeanId,
                             tbean );
            
            if ( tbean instanceof SynchronizableTBean ) {
                synchronizer.registerSynchronizableTBean( tbeanId );
            }
        }

        for ( Iterator tbeanIdIter = this.tbeans.keySet().iterator();
              tbeanIdIter.hasNext(); ) {

            final String tbeanId = (String) tbeanIdIter.next();
            final TBean  tbean   = (TBean) this.tbeans.get( tbeanId );

            Barrier barrier = new Barrier( this.tbeans.size() );

            TBeanThread thread = new TBeanThread( tbeanId,
                                                  tbean,
                                                  synchronizer,
                                                  barrier );

            this.tbeanThreads.add( thread );

            thread.start();
        }
    }

    public void waitForTBeans(SystemTestCase testCase,
                              long timeout)
        throws InterruptedException, WatchdogException {

        long start = new Date().getTime();

        long timeLeft = timeout;

        for ( Iterator threadIter = this.tbeanThreads.iterator();
              threadIter.hasNext(); ) {
            TBeanThread thread = (TBeanThread) threadIter.next();
            thread.join( timeLeft );
            if ( timeout > 0 ) {
                long now = new Date().getTime();
                timeLeft = timeout - (now - start);
            }

            if ( timeLeft <= 0 ) {
                throw new WatchdogException( timeout );
            }
        }
    }

    public void validateTBeans(SystemTestCase testCase,
                               TestResult testResult) {

        for ( Iterator threadIter = this.tbeanThreads.iterator();
              threadIter.hasNext(); ) {
            TBeanThread thread = (TBeanThread) threadIter.next();

            if ( thread.hasError() ) {
                Throwable t = thread.getError();

                if ( t instanceof AssertionFailedError ) {
                    testResult.addFailure( testCase,
                                           (AssertionFailedError) t );
                } else {
                    testResult.addError( testCase,
                                         t );
                }
            } else {
                try {
                    thread.getTBean().assertValid();
                } catch (Throwable t) {
                    if ( t instanceof AssertionFailedError ) {
                        testResult.addFailure( testCase,
                                               (AssertionFailedError) t );
                    } else {
                        testResult.addError( testCase,
                                             t );
                    }
                }
            }
        }
    }
    
    /**
     * @see TBeanManager
     */
    public void tearDownTBeans(SystemTestCase testCase)
        throws Exception {

    }
}
