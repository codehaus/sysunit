package org.sysunit;

public class MockTBeanManager
    implements TBeanManager
{
    private boolean inited;

    private SystemTestCase setUp;
    private SystemTestCase validated;
    private SystemTestCase tornDown;

    public MockTBeanManager() {

    }

    public void init()
        throws Exception {
        this.inited = true;
    }

    public boolean isInited() {
        return this.inited;
    }

    public void setUpTBeans(SystemTestCase testCase)
        throws Exception {
        this.setUp = testCase;
    }

    public SystemTestCase getSetUp() {
        return this.setUp;
    }

    public Throwable[] validateTBeans(SystemTestCase testCase)
        throws InterruptedException {
        this.validated = testCase;

        return new Throwable[0];
    }

    public SystemTestCase getValidated() {
        return this.validated;
    }

    public void tearDownTBeans(SystemTestCase testCase)
        throws Exception {
        this.tornDown = testCase;
    }

    public SystemTestCase getTornDown() {
        return this.tornDown;
    }
}
