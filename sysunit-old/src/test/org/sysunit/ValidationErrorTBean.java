package org.sysunit;

public class ValidationErrorTBean
    extends MockTBean {

    private Throwable error;

    public ValidationErrorTBean(Throwable error) {
        this.error = error;
    }

    public void assertValid()
        throws Throwable {
        throw this.error;
    }
}
