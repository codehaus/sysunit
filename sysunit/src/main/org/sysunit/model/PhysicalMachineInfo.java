package org.sysunit.model;

import java.io.File;
import java.io.Serializable;

public class PhysicalMachineInfo
    implements Serializable
{
    private String[] tags;
    private String[] jdks;

    public PhysicalMachineInfo()
    {
        this( new String[0],
              new String[0] );
    }

    public PhysicalMachineInfo(String[] tags,
                               String[] jdks)
    {
        this.tags = tags;
        this.jdks = jdks;
    }

    public String[] getTags()
    {
        return this.tags;
    }

    public String[] getJdks()
    {
        return this.jdks;
    }
}
