package org.sysunit;

import java.security.SecureClassLoader;

public class TBeanManagerFactoryTest
    extends SysUnitTestCase {

    public void testNewTBeanManager_WithContextClassLoader()
        throws Exception {

        final StringBuffer buffer = new StringBuffer();

        ClassLoader cl = new SecureClassLoader( getClass().getClassLoader() ) {
                public Class loadClass(String name)
                    throws ClassNotFoundException {
                    buffer.append( "load:" + name );
                    return super.loadClass( name );
                }
            };

        Thread.currentThread().setContextClassLoader( cl );

        TBeanManager manager = TBeanManagerFactory.newTBeanManager();

        assertNotNull( manager );

        assertTrue( buffer.toString().indexOf( "load:org.sysunit.local.LocalTBeanManager" ) >= 0 );
    }

    public void testNewTBeanManager_WithNoContextClassLoader()
        throws Exception {

        final StringBuffer buffer = new StringBuffer();

        Thread.currentThread().setContextClassLoader( null );

        TBeanManager manager = TBeanManagerFactory.newTBeanManager();

        assertNotNull( manager );

        assertSame( getClass().getClassLoader(),
                    manager.getClass().getClassLoader() );

    }
}
