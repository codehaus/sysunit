package org.sysunit.model;

import java.io.Serializable;
import java.util.Properties;

public class TBeanInfo
    implements Serializable
{
    private String className;
    private Properties properties;
    private int count;

    public TBeanInfo(String className,
                     Properties properties,
                     int count)
    {
        this.className  = className;
        this.properties = properties;
        this.count      = count;
    }

    public String getClassName()
    {
        return this.className;
    }

    public Properties getProperties()
    {
        return this.properties;
    }

    public int getCount()
    {
        return this.count;
    }
}
