package org.sysunit;

public class DoubleTBeanCase
    extends SystemTestCase {

    private TBean tbeanOne;
    private TBean tbeanTwo;

    public DoubleTBeanCase(TBean tbeanOne,
                           TBean tbeanTwo) {
        this.tbeanOne = tbeanOne;
        this.tbeanTwo = tbeanTwo;
    }

    public TBean tbeanOne() {
        return this.tbeanOne;
    }

    public TBean tbeanTwo() {
        return this.tbeanTwo;
    }
}
