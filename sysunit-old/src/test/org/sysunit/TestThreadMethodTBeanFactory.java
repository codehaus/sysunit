package org.sysunit;

import java.lang.reflect.Method;

public class TestThreadMethodTBeanFactory
    extends SysUnitTestCase {

    public void testConstruct()
        throws Exception {

        SystemTestCase testCase = new SystemTestCase() {
                public void threadOne() {
                }
            };

        Method method = testCase.getClass().getMethod( "threadOne",
                                                       null );

        ThreadMethodTBeanFactory factory = new ThreadMethodTBeanFactory( testCase,
                                                                         method );

        assertSame( testCase,
                    factory.getTestCase() );

        assertSame( method,
                    factory.getThreadMethod() );
    }

    public void testNewTBean()
        throws Exception {
        SystemTestCase testCase = new SystemTestCase() {
                public void threadOne() {
                }
            };

        Method method = testCase.getClass().getMethod( "threadOne",
                                                       null );

        ThreadMethodTBeanFactory factory = new ThreadMethodTBeanFactory( testCase,
                                                                         method );

        TBean tbean = factory.newTBean();

        assertNotNull( tbean );

        assertTrue( tbean instanceof ThreadMethodTBean );

        assertSame( testCase,
                    ((ThreadMethodTBean)tbean).getTestCase() );

        assertSame( method,
                    ((ThreadMethodTBean)tbean).getThreadMethod() );
    }
}
