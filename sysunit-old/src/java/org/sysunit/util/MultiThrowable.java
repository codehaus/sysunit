package org.sysunit.util;

public class MultiThrowable
    extends Throwable {

    private Throwable[] throwables;

    public MultiThrowable(Throwable[] throwables) {
        this.throwables = throwables;
    }

    public Throwable[] getThrowables() {
        return this.throwables;
    }
}
