package org.sysunit.model;

import java.util.Set;
import java.util.HashSet;

public class DistributedSystemTestInfo
{
    private static final JvmInfo[] EMPTY_JVMINFO_ARRAY = new JvmInfo[0];

    private String name;
    private long timeout;

    private Set jvms;

    public DistributedSystemTestInfo(String name)
    {
        this.name = name;
        this.jvms = new HashSet();
    }

    public String getName()
    {
        return this.name;
    }

    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }

    public long getTimeout()
    {
        return this.timeout;
    }

    public void addJvm(JvmInfo jvmInfo)
    {
        this.jvms.add( jvmInfo );
    }

    public JvmInfo[] getJvms()
    {
        return (JvmInfo[]) this.jvms.toArray( EMPTY_JVMINFO_ARRAY );
    }

    public JvmInfo getJvm(String name)
    {
        JvmInfo[] jvms = getJvms();

        for ( int i = 0 ; i < jvms.length ; ++i )
        {
            if ( jvms[ i ].getName().equals( name ) )
            {
                return jvms[ i ];
            }
        }

        return null;
    }

    public int getTotalJvms()
    {
        int num = 0;

        JvmInfo[] jvms = getJvms();

        for ( int i = 0 ; i < jvms.length ; ++i )
        {
            num += jvms[ i ].getCount();
        }

        return num;
    }
}
