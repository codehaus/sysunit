package org.sysunit;

import java.lang.NoSuchMethodException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.HashSet;

public class TestSystemTestCase
    extends SysUnitTestCase {

    public void testConstruct_Bare()
        throws Exception {

        SystemTestCase testCase = new SystemTestCase();

        assertLength( 0,
                      testCase.getTBeanFactories() );

        assertLength( 0,
                      testCase.getTBeanFactoryNames() );
    }

    public void testInit_WithThreadMethodTBeanFactory()
        throws Exception {

        SystemTestCase testCase = new SingleThreadNoOpCase();

        testCase.init();

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
        
        testCase.init();
        
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

    public void testSetUpWatchdog_ZeroTimeout()
        throws Exception {

        SystemTestCase testCase = new SystemTestCase();

        testCase.setUpWatchdog();

        assertNull( testCase.getWatchdog() );
    }

    public void testSetUpWatchdog_NonZeroTimeout()
        throws Exception {

        SystemTestCase testCase = new SystemTestCase() {

                public long getTimeout() {
                    return 1000;
                }

                public void triggerTimeout() {
                    // do nothing
                }
            };

        testCase.setUpWatchdog();

        Thread.sleep( 2000 );

        assertNotNull( testCase.getWatchdog() );

        assertEquals( 1000,
                      testCase.getWatchdog().getTimeout() );
    }

    public void testSetUpWatchDog_NonZeroTimeout_WithMultiplierGreaterThanOne()
        throws Exception {

        System.setProperty( SystemTestCase.WATCHDOG_MULTIPLIER_PROPERTY,
                            "2.0" );

        SystemTestCase testCase = new SystemTestCase() {

                public long getTimeout() {
                    return 1000;
                }

                public void triggerTimeout() {
                    // do nothing
                }
            };

        testCase.setUpWatchdog();

        Thread.sleep( 3000 );

        assertNotNull( testCase.getWatchdog() );

        assertEquals( 2000,
                      testCase.getWatchdog().getTimeout() );
    }
    

    public void testSetUpWatchDog_NonZeroTimeout_WithLessThanOne()
        throws Exception {

        System.setProperty( SystemTestCase.WATCHDOG_MULTIPLIER_PROPERTY,
                            "0.5" );

        SystemTestCase testCase = new SystemTestCase() {

                public long getTimeout() {
                    return 4000;
                }

                public void triggerTimeout() {
                    // do nothing
                }
            };

        testCase.setUpWatchdog();

        Thread.sleep( 3000 );

        assertNotNull( testCase.getWatchdog() );

        assertEquals( 2000,
                      testCase.getWatchdog().getTimeout() );
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
