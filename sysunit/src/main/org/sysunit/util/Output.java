package org.sysunit.util;

import java.io.Serializable;

public class Output
    implements Serializable
{
    private int jvmId;
    private String stdout;
    private String stderr;

    public Output(int jvmId,
                  String stdout,
                  String stderr)
    {
        this.jvmId  = jvmId;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    public int getJvmId()
    {
        return this.jvmId;
    }

    public String getStdout()
    {
        return this.stdout;
    }

    public String getStderr()
    {
        return this.stderr;
    }
}
