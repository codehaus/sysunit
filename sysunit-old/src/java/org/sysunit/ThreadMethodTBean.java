package org.sysunit;

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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Synthetic <code>TBean</code> whose body comes from a <code>SystemTestCase<code>
 * method.
 *
 * @see SystemTestCase
 *
 * @author Bob McWhirter
 *
 * @version $Id$
 */
public class ThreadMethodTBean
    extends AbstractTBean
    implements SynchronizableTBean {

    // ----------------------------------------------------------------------
    //     Constants
    // ----------------------------------------------------------------------

    private static final Log log = LogFactory.getLog(ThreadMethodTBean.class);

    /** Empty <code>Object</code> array. */
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    /** Test-case thread host. */
    private SystemTestCase testCase;

    /** Test-cae thread method. */
    private Method threadMethod;

    // ----------------------------------------------------------------------
    //     Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct.
     *
     * @param testCase The thread method host test-case.
     * @param threadMethod The thread method.
     */
    public ThreadMethodTBean(SystemTestCase testCase,
                             Method threadMethod) {
        this.testCase     = testCase;
        this.threadMethod = threadMethod;
    }

    // ----------------------------------------------------------------------
    //     Instance methods
    // ----------------------------------------------------------------------

    /**
     * Retrieve the <code>SystemTestCase</code>.
     * 
     * @return The test-case.
     */
    public SystemTestCase getTestCase() {
        return this.testCase;
    }

    /**
     * Retrieve the thread <code>Method</code>.
     *
     * @return The thread method.
     */
    public Method getThreadMethod() {
        return this.threadMethod;
    }

    /**
     * @see SynchronizableTBean
     */
    public void setSynchronizer(TBeanSynchronizer synchronizer) {
        getTestCase().setSynchronizer( synchronizer );
    }

    /**
     * Retrieve the <code>TBeanSynchronizer</code>.
     *
     * @return The synchronizer.
     */
    public TBeanSynchronizer getSynchronizer() {
        return getTestCase().getSynchronizer();
    }

    /**
     * @see TBean
     */
    public void run()
        throws Throwable {
        try {
            getThreadMethod().invoke( getTestCase(),
                                      EMPTY_OBJECT_ARRAY );
        } catch (InvocationTargetException e) {
            if ( e.getCause() != null ) {
                throw e.getCause();
            } else {
                throw e;
            }
        }
    }
}
