package org.sysunit.testmesh.master;

import org.sysunit.model.JvmInfo;

import junit.framework.AssertionFailedError;

import java.io.PrintWriter;
import java.io.PrintStream;

public class SlaveThrowable
    extends Throwable
{
    private JvmInfo jvmInfo;
    private String tbeanId;
    private Throwable thrown;
    
    public SlaveThrowable(JvmInfo jvmInfo,
                          String tbeanId,
                          Throwable thrown)
    {
        this.jvmInfo = jvmInfo;
        this.tbeanId = tbeanId;
        this.thrown = thrown;
    }

    public String getLocalizedMessage()
    {
        return this.jvmInfo.getName() + "(" + this.tbeanId + "):" + this.thrown.getLocalizedMessage();
    }

    public String getMessage()
    {
        return this.jvmInfo.getName() + "(" + this.tbeanId + "):" + this.thrown.getMessage();
    }

    public void printStackTrace()
    {
        this.thrown.printStackTrace();
    }

    public void printStackTrace(PrintStream out)
    {
        this.thrown.printStackTrace( out );
    }

    public void printStackTrace(PrintWriter out)
    {
        this.thrown.printStackTrace( out );
    }

    public Throwable getCause()
    {
        return this.thrown;
    }
}
