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

import junit.framework.TestCase;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 * Base for all system tests.
 *
 * <p>
 * A test with multiple threads of execution (possibly distributed
 * across multiple machines) should descend from <code>SystemTestCase</code>.
 * </p>
 *
 * @see TBean
 * @see TBeanManager
 * @see TBeanManagerFactory
 * @see TBeanSynchronizer
 *
 * @author Bob McWhirter
 *
 * @version $Id$
 */
public class SystemTestCase
    extends TestCase {

    // ----------------------------------------------------------------------
    //     Constants
    // ----------------------------------------------------------------------

    /** Prefix for <code>ThreadMethodTBean</code> reflected methods. */
    private static final String THREAD_PREFIX = "thread";

    /** Prefix for <code>FactoryMethodTBeanFactory</code> reflected methods. */
    private static final String TBEAN_PREFIX = "tbean";

    /** Empty <code>String</code> array. */
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    // ----------------------------------------------------------------------
    //     Class members
    // ----------------------------------------------------------------------

    /** Thread-local <code>TBeanSynchronizer</code>. */
    private static ThreadLocal synchronizer = new ThreadLocal();

    // ----------------------------------------------------------------------
    //     Instance members
    // ----------------------------------------------------------------------

    /** <code>TBeanFactory</code> indexed by <code>String</code> identifier. */
    private Map tbeanFactories;

    /** <code>TBeanManager</code> used for this test-run.  Only applicable
     *  to head test case if distributed. */
    private TBeanManager tbeanManager;

    // ----------------------------------------------------------------------
    //     Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct.
     */
    public SystemTestCase() {
        this.tbeanFactories = new HashMap();
    }

    /**
     * Construct.
     *
     * @param testName The name of the test to run. 
     */
    public SystemTestCase(String testName) {
        super( testName );
        this.tbeanFactories = new HashMap();
    }

    // ----------------------------------------------------------------------
    //     Instance methods
    // ----------------------------------------------------------------------

    /**
     * Add a <code>TBeanFactory</code> to this test.
     *
     * @param name The unique name of the factory.
     * @param tbeanFactory The TBean factory.
     */
    public void addTBeanFactory(String name,
                                TBeanFactory tbeanFactory) {
        this.tbeanFactories.put( name,
                                 tbeanFactory );
    }

    /**
     * Retrieve all <code>TBeanFactory</code>s.
     *
     *  @return The array of TBeanFactory objects associated
     *          with this test.
     */
    public TBeanFactory[] getTBeanFactories() {
        return (TBeanFactory[]) this.tbeanFactories.values().toArray( TBeanFactory.EMPTY_ARRAY );
    }

    /**
     * Retrieve a <code>TBeanFactory</code> by name.
     *
     * @param name The unique TBeanFactory name.
     *
     * @return The associated TBeanFactory or <code>null</code>.
     */
    public TBeanFactory getTBeanFactory(String name) {
        return (TBeanFactory) this.tbeanFactories.get( name );
    }

    /**
     * Retrieve an array of <code>TBeanFactory</code> names.
     *
     *  @return The possibly empty array of registered TBeanFactory names.
     */
    public String[] getTBeanFactoryNames() {
        return (String[]) this.tbeanFactories.keySet().toArray( EMPTY_STRING_ARRAY );
    }

    /**
     * Initialize reflected <code>TBeanFactory</code> objects.
     *
     * <p>
     * Public non-static methods with no parameters matching the
     * pattern of <b>threadXXXXX()</b> and <b>tbeanXXXXX()</b>
     * are reflected to register <code>ThreadMethodTBeanFactory</code>
     * and <code>FactoryMethodTBeanFactory</code> objects with
     * the test.
     * </p>
     *
     * @throws InvalidMethodException If a method matching the naming
     *         patterns does not satisfy other conditions, such as public,
     *         non-static, no-arg and return type.
     */
    void init()
        throws InvalidMethodException {

        Set names = new HashSet();

        Method[] methods = getClass().getMethods();

        for ( int i = 0 ; i < methods.length ; ++i ) {
            String methodName = methods[i].getName();
            if ( methodName.startsWith( TBEAN_PREFIX ) ) {
                verifyNonStatic( methods[i] );
                verifyNoParameters( methods[i] );
                verifyTBeanReturnType( methods[i] );
                String name = methodName.substring( TBEAN_PREFIX.length() );
                verifyNoDupes( methods[i],
                               names,
                               name );
                addTBeanFactory( name,
                                 new FactoryMethodTBeanFactory( this,
                                                                methods[i] ) );
            } else if ( methodName.startsWith( THREAD_PREFIX ) ) {
                verifyNonStatic( methods[i] );
                verifyNoParameters( methods[i] );
                verifyVoidReturnType( methods[i] );
                String name = methodName.substring( THREAD_PREFIX.length() );
                verifyNoDupes( methods[i],
                               names,
                               name );
                addTBeanFactory( name,
                                 new ThreadMethodTBeanFactory( this,
                                                               methods[i] ) );
            }
        }
    }

    static void verifyNoDupes(Method method,
                              Set names,
                              String name) 
        throws InvalidMethodException {
        if ( names.contains( name ) ) {
            throw new InvalidMethodException( method,
                                              "name '" + name + "' is a duplicate" );
        }
    }

    /**
     * Verify that a <code>Method</code> is not static.
     *
     * @param method The method.
     *
     * @throws InvalidMethodException If the method is static.
     */
    static void verifyNonStatic(Method method)
        throws InvalidMethodException {
        if ( Modifier.isStatic( method.getModifiers() ) ) {
            throw new InvalidMethodException( method,
                                              "must be non-static" );
        }
    }

    /**
     * Verify that a <code>Method</code> has no parameters.
     *
     * @param method The method.
     *
     * @throws InvalidMethodException If the method has parameters.
     */
    static void verifyNoParameters(Method method)
        throws InvalidMethodException {
        if ( method.getParameterTypes().length != 0 ) {
            throw new InvalidMethodException( method,
                                              "must have no parameters" );
        }
    }

    /**
     * Verify that a <code>Method</code> has a void return type.
     *
     * @param method The method.
     *
     * @throws InvalidMethodException If the method does not have a void
     *         return type.
     */
    static void verifyVoidReturnType(Method method)
        throws InvalidMethodException {
        if ( ! method.getReturnType().equals( Void.TYPE ) ) {
            throw new InvalidMethodException( method,
                                              "must have void return type" );
        }
    }

    /**
     * Verify that a <code>Method</code> returns a <code>TBean</code> or subclass.
     *
     * @param method The method.
     *
     * @throws InvalidMethodException If the method does not return a
     *         <code>TBean</code> or subclass.
     */
    static void verifyTBeanReturnType(Method method)
        throws InvalidMethodException {
        if ( ! TBean.class.isAssignableFrom( method.getReturnType() ) ) {
            throw new InvalidMethodException( method,
                                              "must return a TBean or subclass" );
        }
    }

    /**
     * @see TestCase
     */
    public void runBare()
        throws Throwable {
        init();
        setUpTBeans();
        try { 
            super.runBare();

            Throwable[] errors = validateTBeans();

            if ( errors.length > 0 )
            {
                for ( int i = 0 ; i < errors.length ; ++i )
                {
                    if ( errors[i] instanceof InvocationTargetException )
                    {
                        errors[i] = ((InvocationTargetException)errors[i]).getTargetException();
                    }
                }
                throw new MultiThrowable( errors );
            }
        } finally {
            tearDownTBeans();
        }
    }

    /**
     * Set up the <code>TBean</code>s.
     *
     * @throws Exception If an error occurs.
     */
    protected void setUpTBeans()
        throws Exception {
        getTBeanManager().setUpTBeans( this );
    }

    protected Throwable[] validateTBeans()
        throws Exception {
        return getTBeanManager().validateTBeans( this );
    }

    /**
     * Tear down the <code>TBean</code>s.
     *
     * @throws Exception If an error occurs.
     */
    protected void tearDownTBeans()
        throws Exception {
        getTBeanManager().tearDownTBeans( this );
    }

    /**
     * Retrieve the <code>TBeanManager</code>.
     *
     * <p>
     * This method may create a new <code>TBeanManager</code>
     * using the <code>TBeanManagerFactory</code> the first
     * time it is called.
     * </p>
     *
     * @see TBeanManagerFactory
     *
     * @return The TBeanManager.
     *
     * @throws Exception If an error occurs while attempting
     *         to instantiate the TBeanManager.
     */
    protected TBeanManager getTBeanManager()
        throws Exception {
        if ( this.tbeanManager == null ) {
            setTBeanManager( TBeanManagerFactory.newTBeanManager() );
        }

        return this.tbeanManager;
    }

    protected void setTBeanManager(TBeanManager tbeanManager) {
        this.tbeanManager = tbeanManager;
    }

    // ----------------------------------------------------------------------

    /**
     * Synchronize a synthesized <code>ThreadMethodTBean</code> at
     * a sync-point.
     *
     * <p>
     * This method is usable <b>only</b> by reflected/synthesized
     * <code>ThreadMethodTBean</code> instances.  It replaces the
     * <code>sync(...)</code> method in <code>AbstractSynchronizableTBean</code>.
     * </p>
     *
     * <p>
     * This method relies upon <code>ThreadLocal</code> data-structures
     * that are configured by the test-runner.
     * </p>
     *
     * @see AbstractSynchronizableTBean
     *
     * @param syncPoint The sync-point.
     *
     * @throws SynchronizationException If an error occurs while attempting
     *         to perform synchronization.
     * @throws InterruptedException If the synchronization is interrupted.
     */
    protected void sync(String syncPoint)
        throws SynchronizationException, InterruptedException {
        getSynchronizer().sync( syncPoint );
    }

    /**
     * Set the <code>TBeanSynchronizer</code> for the calling
     * thread.
     *
     * <p>
     * This method affects <code>ThreadLocal</code> data-structures
     * and <b>must</b> be called by the thread that will subsequently
     * make calls to {@link #sync}.
     * </p>
     *
     * @see #sync
     * @see #getSynchronizer
     *
     * @param synchronizer The synchronizer for the calling thread.
     */
    void setSynchronizer(TBeanSynchronizer synchronizer) {
        SystemTestCase.synchronizer.set( synchronizer );
    }

    /**
     * Retrieve the <code>TBeanSynchronizer</code> for the calling
     * thread.
     *
     * <p>
     * This method uses <code>ThreadLocal</code> data-structures
     * and <b>must</b> be called by the thread that previously
     * configured the <code>TBeanSynchronizer</code> through a
     * call to {@link #setSynchronizer}.
     * </p>
     *
     * @see #sync
     * @see #setSynchronizer
     *
     * @return The synchronizer for the calling thread.
     */
    TBeanSynchronizer getSynchronizer() {
        return (TBeanSynchronizer) SystemTestCase.synchronizer.get();
    }

    /** 
     * Default test.
     *
     * <p>
     * This test, by default, performs nothing and only
     * calls {@link #assertValid}, which itself is defaultly
     * a no-op.  This structure allows the simplest tests to
     * be completely contained within the TBeans or threads.
     * </p>
     *
     * @see #assertValid
     *
     * @throws Exception If an error occurs.
     */
    public void testSystem() throws Exception {
        assertValid();
    }

    /**
     * Perform default post-validation assertion.
     *
     * @see #testSystem
     *
     * @throws Exception If an error occurs.
     */
    public void assertValid() throws Exception {
        // intentionally left blank
    }
}
