package org.sysunit.testmesh.master;

import org.sysunit.model.JvmInfo;

public class ThrowEntry
{
    private JvmInfo jvmInfo;
    private String tbeanId;
    private Throwable thrown;

    public ThrowEntry(JvmInfo jvmInfo,
                      String tbeanId,
                      Throwable thrown)
    {
        this.jvmInfo = jvmInfo;
        this.tbeanId = tbeanId;
        this.thrown  = thrown;
    }

    public JvmInfo getJvmInfo()
    {
        return this.jvmInfo;
    }

    public String getTBeanId()
    {
        return this.tbeanId;
    }

    public Throwable getThrown()
    {
        return this.thrown;
    }
}
