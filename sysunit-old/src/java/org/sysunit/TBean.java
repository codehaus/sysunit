/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit;

/**
 * <p>A <code>TBean</code></b> is a testing bean used in a SysUnit TestCase.
 * TBeans are typically remote such as remote MBeans, session beans, use remote proxies
 * or execute remote scripts which make up the whole distributed test case
 * 
 * @author James Strachan
 * @author Bob McWhirter
 * @version $Revision$
 */
public interface TBean {

    /** Empty array. */
    static final TBean[] EMPTY_ARRAY = new TBean[0];
    
    /**
     * Called before each test run to initialise the TBean
     * 
     * @throws Exception If an error occurs while attempting
     *         to set up the TBean.
     */
    void setUp() throws Exception;
    
    /**
     * Called in parallel by the SysUnit test case to run the TBean
     * 
     * @throws Throwable If an error occurs while attempting to run
     *         the TBean.
     */
    void run() throws Throwable;
    
    /**
     * Called after the run() methods of all the TBeans have completed 
     * (or timed out) to assert that each TBean is in a valid state
     * 
     * @throws Exception If an error occurs while attempting
     *         validate the TBean.
     */
    void assertValid() throws Throwable;
    
    /**
     * Called after the SysUnit test has run to clean up any resources such as 
     * database connections, to close any files etc
     * 
     * @throws Exception If an error occurs while attempting
     *         to tear down the TBean.
     */
    void tearDown() throws Exception;

}
