package org.sysunit;

public class ErrorTBean
    extends MockTBean {

    private Throwable error;

    public ErrorTBean(Throwable error) {
        this.error = error;
    }

    public void run()
        throws Throwable {
        super.run();
        throw this.error;
    }
}
