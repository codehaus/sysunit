package org.sysunit.util;

import org.sysunit.SysUnitException;

public class InvalidPropertyException
    extends SysUnitException
{
    private Class beanClass;
    private String propertyName;

    public InvalidPropertyException(Class beanClass,
                                    String propertyName)
    {
        this.beanClass    = beanClass;
        this.propertyName = propertyName;
    }

    public Class getBeanClass()
    {
        return this.beanClass;
    }

    public String getPropertyName()
    {
        return this.propertyName;
    }
}
