package org.sysunit;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class SystemTestCaseTest
    extends SysUnitTestCase {

    public void testConstruct_Bare()
        throws Exception {

        SystemTestCase testCase = new SystemTestCase();

        assertLength( 0,
                      testCase.getTBeanFactories() );

        assertLength( 0,
                      testCase.getTBeanFactoryNames() );

        assertEquals( 0,
                      testCase.getTimeout() );

        assertEquals( 1.0,
                      testCase.getTimeoutMultiplier(),
                      0.0 );

        assertEquals( 0,
                      testCase.getAdjustedTimeout() );

        assertEquals( 1,
                      testCase.countTestCases() );

        TBeanManager tbeanManager = testCase.getTBeanManager();

        assertNotNull( tbeanManager );

        assertSame( tbeanManager,
                    testCase.getTBeanManager() );
    }

    public void testInit_WithThreadMethodTBeanFactory()
        throws Exception {

        SystemTestCase testCase = new SingleThreadNoOpCase();

        testCase.initializeFactories();

        assertLength( 1,
                      testCase.getTBeanFactoryNames() );

        assertLength( 1,
                      testCase.getTBeanFactories() );

        assertEquals( "One",
                      testCase.getTBeanFactoryNames()[0] );

        assertTrue( testCase.getTBeanFactories()[0] instanceof ThreadMethodTBeanFactory );

        assertSame( testCase.getTBeanFactories()[0],
                    testCase.getTBeanFactory( "One" ) );
    }

    public void testInit_WithFactoryMethodTBeanFactory()
        throws Exception {

        SystemTestCase testCase = new SingleTBeanNullCase();
        
        testCase.initializeFactories();
        
        assertLength( 1,
                      testCase.getTBeanFactoryNames() );

        assertLength( 1,
                      testCase.getTBeanFactories() );

        assertEquals( "One",
                      testCase.getTBeanFactoryNames()[0] );

        assertTrue( testCase.getTBeanFactories()[0] instanceof FactoryMethodTBeanFactory );

        assertSame( testCase.getTBeanFactories()[0],
                    testCase.getTBeanFactory( "One" ) );
    }

    public void testVerifyNoDupes()
        throws Exception {

        Set names = new HashSet();

        Method method = getMethod( "methodCheese" );

        try {
            SystemTestCase.verifyNoDupes( method,
                                          names,
                                          "Cheese" );
        } catch (InvalidMethodException e) {
            fail( e.getMessage() );
        }

        names.add( "Cheese" );

        try {
            SystemTestCase.verifyNoDupes( method,
                                          names,
                                          "Cheese" );
            fail( "Should have thrown InvalidMethodException" );
        } catch (InvalidMethodException e) {
            // expected and correct
            assertSame( method,
                        e.getMethod() );
        }
    }

    public void testVerifyNonStatic()
        throws Exception {

        Method method = getMethod( "methodCheese" );

        try {
            SystemTestCase.verifyNonStatic( method );
        } catch (InvalidMethodException e) {
            fail( e.getMessage() );
        }

        method = getMethod( "methodTacos" );

        try {
            SystemTestCase.verifyNonStatic( method );
            fail( "should have thrown InvalidMethodException" );
        } catch (InvalidMethodException e) {
            // expected and correct
            assertSame( method,
                        e.getMethod() );
        }
    }

    public void testVerifyNoParameters()
        throws Exception {

        Method method = getMethod( "methodCheese" );

        try {
            SystemTestCase.verifyNoParameters( method );
        } catch (InvalidMethodException e) {
            fail( e.getMessage() );
        }

        method = getMethod( "methodCat" );

        try {
            SystemTestCase.verifyNoParameters( method );
            fail( "should have thrown InvalidMethodException" );
        } catch (InvalidMethodException e) {
            // expected and correct
            assertSame( method,
                        e.getMethod() );
        }
    }

    public void testVerifyVoidReturnType()
        throws Exception {

        Method method = getMethod( "methodCheese" );

        try {
            SystemTestCase.verifyVoidReturnType( method );
        } catch (InvalidMethodException e) {
            fail( e.getMessage() );
        }

        method = getMethod( "methodDog" );

        try {
            SystemTestCase.verifyVoidReturnType( method );
            fail( "should have thrown InvalidMethodException" );
        } catch (InvalidMethodException e) {
            // expected and correct
            assertSame( method,
                        e.getMethod() );
        }
    }

    public void testVerifyTBeanReturnType()
        throws Exception {

        Method method = getMethod( "methodFish" );

        try {
            SystemTestCase.verifyTBeanReturnType( method );
        } catch (InvalidMethodException e) {
            fail( e.getMessage() );
        }

        method = getMethod( "methodDog" );

        try {
            SystemTestCase.verifyTBeanReturnType( method );
            fail( "should have thrown InvalidMethodException" );
        } catch (InvalidMethodException e) {
            // expected and correct
            assertSame( method,
                        e.getMethod() );
        }
    }

    /*
    public void testSetUpTBeans()
        throws Exception {

        MockTBeanManager tbeanManager = new MockTBeanManager();

        SystemTestCase testCase = new SystemTestCase();

        testCase.setTBeanManager( tbeanManager );

        assertSame( tbeanManager,
                    testCase.getTBeanManager() );

        testCase.setUpTBeans();

        assertSame( testCase,
                    tbeanManager.getSetUp() );

        assertNull( tbeanManager.getTornDown() );
    }

    public void testTearDownTBeans()
        throws Exception {

        MockTBeanManager tbeanManager = new MockTBeanManager();

        SystemTestCase testCase = new SystemTestCase();

        testCase.setTBeanManager( tbeanManager );

        assertSame( tbeanManager,
                    testCase.getTBeanManager() );

        testCase.tearDownTBeans();

        assertSame( testCase,
                    tbeanManager.getTornDown() );

        assertNull( tbeanManager.getSetUp() );
    }
    */

    public void testGetTBeanManager_ViaFactory()
        throws Exception {

        SystemTestCase testCase = new SystemTestCase();

        assertNotNull( testCase.getTBeanManager() );
    }

    public void testGetTBeanManager_ViaFactory_Bogus()
        throws Exception {
        System.setProperty( TBeanManager.class.getName(),
                            "cheese" );

        SystemTestCase testCase = new SystemTestCase();

        try {
            testCase.getTBeanManager();
            fail( "should have thrown Exception" );
        } catch (Exception e) {
            // expected and correct
        }
    }

    public void testTimeout_Default() {
        SystemTestCase testCase = new SystemTestCase();

        assertEquals( 0,
                      testCase.getTimeout() );

        assertEquals( 0,
                      testCase.getAdjustedTimeout() );
    }

    public void testTimeout_Overridden() {
        SystemTestCase testCase = new SystemTestCase() {
                public long getTimeout() {
                    return 1500;
                }
            };

        assertEquals( 1500,
                      testCase.getTimeout() );

        assertEquals( 1500,
                      testCase.getAdjustedTimeout() );
    }

    public void testTimeout_Multiplier_Default() {
        System.setProperty( SystemTestCase.TIMEOUT_MULTIPLIER_PROPERTY,
                            "42" );

        SystemTestCase testCase = new SystemTestCase();

        assertEquals( 0,
                      testCase.getTimeout() );

        assertEquals( 42.0,
                      testCase.getTimeoutMultiplier(),
                      0.0 );

        assertEquals( 0,
                      testCase.getAdjustedTimeout() );
    }

    public void testTimeout_Multiplier_Overridden() {
        System.setProperty( SystemTestCase.TIMEOUT_MULTIPLIER_PROPERTY,
                            "42" );

        SystemTestCase testCase = new SystemTestCase() {
                public long getTimeout() {
                    return 1000;
                }
            };

        assertEquals( 1000,
                      testCase.getTimeout() );
        
        assertEquals( 42.0,
                      testCase.getTimeoutMultiplier(),
                      0.0 );

        assertEquals( 42000,
                      testCase.getAdjustedTimeout() );
    }

    public void testAssertValid()
        throws Exception {

        SystemTestCase testCase = new SystemTestCase();

        // shouldn't do anything bad by default

        testCase.assertValid();
                                                           
    }

    public TBean methodFish() {
        return null;
    }

    public int methodDog() {
        return 42;
    }

    public void methodCheese() {

    }

    public static void methodTacos() {

    }

    public void methodCat(int foo) {

    }

    protected Method getMethod(String name)
        throws NoSuchMethodException {

        Method[] methods = getClass().getMethods();

        for ( int i = 0 ; i < methods.length ; ++i ) {
            if ( methods[i].getName().equals( name ) ) {
                return methods[i];
            }
        }

        throw new NoSuchMethodException( name );
    }

}
