package org.sysunit.model;

import org.sysunit.SystemTestCase;

import java.lang.reflect.Method;
import java.io.Serializable;

public class ThreadInfo
    implements Serializable
{
    private SystemTestCase systemTestCase;
    private Method method;

    public ThreadInfo(SystemTestCase systemTestCase,
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
