package org.sysunit.model;

import org.sysunit.SystemTestCase;

import java.io.Serializable;
import java.lang.reflect.Method;

public class TBeanFactoryInfo
    implements Serializable
{
    private SystemTestCase systemTestCase;
    private Method method;

    public TBeanFactoryInfo(SystemTestCase systemTestCase,
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
}
