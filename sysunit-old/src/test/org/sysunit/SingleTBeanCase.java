package org.sysunit;

public class SingleTBeanCase
    extends SystemTestCase {

    private TBean tbean;

    public SingleTBeanCase(TBean tbean) {
        this.tbean = tbean;
    }

    public TBean tbeanOne() {
        return this.tbean;
    }
}
