package org.sysunit.model;

import java.util.Map;
import java.util.HashMap;

public class ScenarioInfo
{
    private String name;
    private DistributedSystemTestInfo testInfo;
    private Map tags;
    private Map jdks;

    public ScenarioInfo(String name,
                        DistributedSystemTestInfo testInfo)
    {
        this.name     = name;
        this.testInfo = testInfo;
        this.tags = new HashMap();
        this.jdks = new HashMap();
    }

    public DistributedSystemTestInfo getSystemTestInfo()
    {
        return this.testInfo;
    }

    public String getName()
    {
        return this.name;
    }

    public void setTag(JvmInfo jvm,
                       String tag)
    {
        this.tags.put( jvm,
                       tag );
    }

    public String getTag(JvmInfo jvm)
    {
        if ( this.tags.containsKey( jvm ) )
        {
            return (String) this.tags.get( jvm );
        }

        return "*";
    }

    public void setJdk(JvmInfo jvm,
                       String jdk)
    {
        this.jdks.put( jvm,
                       jdk );
    }

    public String getJdk(JvmInfo jvm)
    {
        if ( this.jdks.containsKey( jvm ) )
        {
            return (String) this.jdks.get( jvm );
        }

        return "*";
    }
}
