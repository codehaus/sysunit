package org.sysunit;

import junit.framework.TestResult;

public class MockTBeanManager
    implements TBeanManager
{
    private boolean inited;

    private SystemTestCase started;
    private SystemTestCase waited;
    private long timeout;
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

    public void startTBeans(SystemTestCase testCase,
                            TestResult testResult)
        throws Exception {
        this.started = testCase;
    }

    public SystemTestCase getStarted() {
        return this.started;
    }

    public void waitForTBeans(SystemTestCase testCase,
                              long timeout) {
        this.waited = testCase;
        this.timeout = timeout;
    }

    public SystemTestCase getWaited() {
        return this.waited;
    }

    public long getTimeout() {
        return this.timeout;
    }

    public void validateTBeans(SystemTestCase testCase,
                               TestResult testResult) {
        this.validated = testCase;
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
