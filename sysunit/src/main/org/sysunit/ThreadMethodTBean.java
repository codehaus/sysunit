package org.sysunit;

import java.lang.reflect.Method;

public class ThreadMethodTBean
    implements SynchronizableTBean
{
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    private SystemTestCase systemTestCase;
    private Method method;

    private TBeanSynchronizer synchronizer;
    
    ThreadMethodTBean(SystemTestCase systemTestCase,
                      Method method)
    {
        this.systemTestCase = systemTestCase;
        this.method         = method;
    }

    public SystemTestCase getSystemTestCase()
    {
        return this.systemTestCase;
    }

    public Method getMethod()
    {
        return this.method;
    }

    public void setUp()
    {
    }

    public void tearDown()
    {
    }

    public void run()
        throws Exception
    {
        getMethod().invoke( getSystemTestCase(),
                            EMPTY_OBJECT_ARRAY );
    }

    public void assertValid()
    {
    }

    public void setSynchronizer(TBeanSynchronizer synchronizer)
    {
        this.synchronizer = synchronizer;
    }

    public TBeanSynchronizer getSynchronizer()
    {
        return this.synchronizer;
    }
}
