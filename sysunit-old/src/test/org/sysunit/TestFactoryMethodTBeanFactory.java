package org.sysunit;

import java.lang.reflect.Method;

public class TestFactoryMethodTBeanFactory
    extends SysUnitTestCase {

    public void testConstruct()
        throws Exception {

        final TBean tbean = new NoOpTBean();

        SystemTestCase testCase = new SystemTestCase() {
                public TBean tbeanFish()
                    throws Exception {
                    return tbean;
                }
            };

        Method method = testCase.getClass().getMethod( "tbeanFish",
                                                       new Class[0] );

        FactoryMethodTBeanFactory factory = new FactoryMethodTBeanFactory( testCase,
                                                                           method );

        assertEquals( testCase,
                      factory.getTestCase() );

        assertSame( method,
                    factory.getFactoryMethod() );

        assertSame( tbean,
                    factory.newTBean() );
                    
    }

}
