package org.sysunit.testmesh.slavehost;

import org.sysunit.SysUnitException;

public class SlaveHostConfigurationException
    extends SysUnitException
{
    private String line;
    public SlaveHostConfigurationException(String line)
    {
        this.line = line;
    }
}
