package org.sysunit.model;

import org.sysunit.TBean;
import org.sysunit.SystemTestCase;
import org.sysunit.FactoryMethodTBeanFactory;
import org.sysunit.ThreadMethodTBeanFactory;
import org.sysunit.InvalidMethodException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.HashSet;

public class SimpleBuilder
    extends AbstractBuilder {

    /** Prefix for <code>ThreadMethodTBean</code> reflected methods. */
    private static final String THREAD_PREFIX = "thread";

    /** Prefix for <code>FactoryMethodTBeanFactory</code> reflected methods. */
    private static final String TBEAN_PREFIX = "tbean";

    
    public SimpleBuilder() {

    }

    public TestInfo build(SystemTestCase testCase)
        throws Exception {
        newTestInfo( testCase.getClass().getName() );
        newLogicalMachineInfo( "default" );

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
                newTBeanInfo( name,
                              new FactoryMethodTBeanFactory( testCase,
                                                             methods[i] ) );
            } else if ( methodName.startsWith( THREAD_PREFIX ) ) {
                verifyNonStatic( methods[i] );
                verifyNoParameters( methods[i] );
                verifyVoidReturnType( methods[i] );
                String name = methodName.substring( THREAD_PREFIX.length() );
                verifyNoDupes( methods[i],
                               names,
                               name );
                newTBeanInfo( name,
                              new ThreadMethodTBeanFactory( (SystemTestCase) testCase.getClass().newInstance(),
                                                            methods[i] ) );
            }
        }

        return getTestInfo();
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
}
