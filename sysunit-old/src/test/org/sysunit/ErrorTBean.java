package org.sysunit;

public class ErrorTBean
    extends MockTBean {

    private Exception error;

    public ErrorTBean(Exception error) {
        this.error = error;
    }

    public void run()
        throws Exception {
        super.run();
        throw this.error;
    }
}
