package org.sysunit;

import java.lang.reflect.Method;

public class TestThreadMethodTBean
    extends SysUnitTestCase {

    public void testConstruct()
        throws Exception {

        Test test = new Test();
        Method threadMethod = test.getClass().getMethod( "threadOne",
                                                         null );

        ThreadMethodTBean tbean = new ThreadMethodTBean( test,
                                                         threadMethod );

        assertSame( test,
                    tbean.getTestCase() );

        assertSame( threadMethod,
                    tbean.getThreadMethod() );
    }

    public void testRun()
        throws Throwable {

        Test test = new Test();
        Method threadMethod = test.getClass().getMethod( "threadOne",
                                                                     null );

        ThreadMethodTBean tbean = new ThreadMethodTBean( test,
                                                         threadMethod );

        assertFalse( test.threadOneTouched );

        tbean.run();

        assertTrue( test.threadOneTouched );
    }

    public void testSynchronizer() {

        Test test = new Test();
        ThreadMethodTBean tbean = new ThreadMethodTBean( test,
                                                         null );

        TBeanSynchronizer sync = new MockTBeanSynchronizer();

        tbean.setSynchronizer( sync );

        assertSame( sync,
                    tbean.getSynchronizer() );
    }

    // ----------------------------------------------------------------------

    public static class Test
        extends SystemTestCase {
        
        public boolean threadOneTouched = false;
        
        public void threadOne() {
            threadOneTouched = true;
        }
    }

}
