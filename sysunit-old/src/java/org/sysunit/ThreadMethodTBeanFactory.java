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

import java.lang.reflect.Method;

/**
 * <code>TBeanFactory</code> implementation which uses a static method
 * to synthesize simple <code>TBean</code>s.
 *
 * <p>
 * By using the body of the static method as the logic of the <code>TBean</code>,
 * the <code>ThreadMethodTBeanFactory</code> creates <code>ThreadMethodTBean</code>s
 * for execution.  This allows simple <code>TBean</code> synthesizing without
 * having to write actual <code>TBean</code> implementations.
 * </p>
 *
 * @see TBean
 * @see ThreadMethodTBean
 *
 * @author Bob McWhirter
 *
 * @version $Id$
 */
public class ThreadMethodTBeanFactory
    implements TBeanFactory {

    // ----------------------------------------------------------------------
    //     Constants
    // ----------------------------------------------------------------------

    /** Empty <code>Object</code> array. */
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    // ----------------------------------------------------------------------
    //     Instance members
    // ----------------------------------------------------------------------

    /** Test-case. */
    private SystemTestCase testCase;

    /** Thread TBean body method. */
    private Method threadMethod;
    
    // ----------------------------------------------------------------------
    //     Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct.
     *
     * @param testCase The test-case.
     * @param threadMethod Static TBean thread body method.
     */
    public ThreadMethodTBeanFactory(SystemTestCase testCase,
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
     * Retrieve the static thread body <code>Method</code>.
     *
     * @return The thread body method.
     */
    public Method getThreadMethod() {
        return this.threadMethod;
    }

    /**
     * @see TBeanFactory
     */
    public TBean newTBean() 
        throws Exception {
        return new ThreadMethodTBean( getTestCase(),
                                      getThreadMethod() );
    }
}
