package org.sysunit.testmesh.master;

public abstract class TestThrewCommand
    extends MasterCommand
{
    private int jvmId;
    private String tbeanId;
    private Throwable thrown;

    public TestThrewCommand(int jvmId,
                            String tbeanId,
                            Throwable thrown)
    {
        this.jvmId   = jvmId;
        this.tbeanId = tbeanId;
        this.thrown  = thrown;
    }

    public int getJvmId()
    {
        return this.jvmId;
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

