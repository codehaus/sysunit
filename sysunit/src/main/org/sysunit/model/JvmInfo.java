package org.sysunit.model;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;

public class JvmInfo
    implements Serializable
{
    private static final TBeanInfo[] EMPTY_TBEANINFO_ARRAY = new TBeanInfo[0];

    private String name;
    private int count;

    private Set threads;
    private Set tbeans;
    private Set tbeanFactories;

    public JvmInfo(String name,
                   int count)
    {
        this.name           = name;
        this.count          = count;
        this.threads        = new HashSet();
        this.tbeans         = new HashSet();
        this.tbeanFactories = new HashSet();
    }

    public String getName()
    {
        return this.name;
    }

    public int getCount()
    {
        return this.count;
    }

    public void addTBean(TBeanInfo tbeanInfo)
    {
        this.tbeans.add( tbeanInfo );
    }

    public TBeanInfo[] getTBeans()
    {
        return (TBeanInfo[]) this.tbeans.toArray( EMPTY_TBEANINFO_ARRAY );
    }

    public String toString()
    {
        return "[JvmInfo: name=" + this.name + "; count=" + this.count + "]";
    }
}
