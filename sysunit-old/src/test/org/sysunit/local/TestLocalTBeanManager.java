package org.sysunit.local;

import org.sysunit.SysUnitTestCase;

public class TestLocalTBeanManager
    extends SysUnitTestCase {

    public void testConstruct() {
        LocalTBeanManager manager = new LocalTBeanManager();

        assertEmpty( manager.getTBeanThreads() );
        assertEmpty( manager.getTBeans() );
    }

}
