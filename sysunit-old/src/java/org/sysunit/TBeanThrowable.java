package org.sysunit;

import java.io.PrintStream;
import java.io.PrintWriter;

public class TBeanThrowable
    extends Throwable
{
    private String tbeanId;
    private Throwable error;

    public TBeanThrowable(String tbeanId,
                          Throwable error) {
        this.tbeanId = tbeanId;
        this.error   = error;
    }

    public String getTBeanId() {
        return this.tbeanId;
    }

    public Throwable getError() {
        return this.error;
    }

    public String getMessage() {
        return "TBean: " + getTBeanId() + ": " + getError().getMessage();
    }

    public void printStackTrace() {
        this.error.printStackTrace();
    }

    public void printStackTrace(PrintStream out) {
        this.error.printStackTrace( out );
    }

    public void printStackTrace(PrintWriter out) {
        this.error.printStackTrace( out );
    }
}
