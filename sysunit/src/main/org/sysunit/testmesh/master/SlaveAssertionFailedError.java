package org.sysunit.testmesh.master;

import org.sysunit.model.JvmInfo;

import junit.framework.AssertionFailedError;

import java.io.PrintWriter;
import java.io.PrintStream;

public class SlaveAssertionFailedError
    extends AssertionFailedError
{
    private JvmInfo jvmInfo;
    private String tbeanId;
    private AssertionFailedError error;
    
    public SlaveAssertionFailedError(JvmInfo jvmInfo,
                                     String tbeanId,
                                     AssertionFailedError error)
    {
        this.jvmInfo = jvmInfo;
        this.tbeanId = tbeanId;
        this.error = error;
    }

    public String getLocalizedMessage()
    {
        return this.jvmInfo.getName() + "(" + this.tbeanId + "): " + this.error.getClass().getName() + ": " + this.error.getLocalizedMessage();
    }

    public String getMessage()
    {
        return this.jvmInfo.getName() + "(" + this.tbeanId + "): " + this.error.getClass().getName() + ": " + this.error.getMessage();
    }

    public void printStackTrace()
    {
        this.error.printStackTrace();
    }

    public void printStackTrace(PrintStream out)
    {
        this.error.printStackTrace( out );
    }

    public void printStackTrace(PrintWriter out)
    {
        this.error.printStackTrace( out );
    }

    public Throwable getCause()
    {
        return this.error;
    }
}
