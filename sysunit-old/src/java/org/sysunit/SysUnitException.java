package org.sysunit;

public class SysUnitException
    extends Exception {

    private Throwable rootCause;

    public SysUnitException() {

    }

    public SysUnitException(Throwable rootCause) {
        this.rootCause = rootCause;
    }
}
