package org.sysunit.command.master;

import org.sysunit.command.Dispatcher;

public class TestNodeInfo {

    public static final TestNodeInfo[] EMPTY_ARRAY = new TestNodeInfo[0];

    private String testNodeName;
    private int numSynchronizableTBeans;
    private Dispatcher dispatcher;

    public TestNodeInfo(String testNodeName,
                        int numSynchronizableTBeans,
                        Dispatcher dispatcher) {
        this.testNodeName = testNodeName;
        this.numSynchronizableTBeans = numSynchronizableTBeans;
        this.dispatcher = dispatcher;
    }

    public String getTestNodeName() {
        return this.testNodeName;
    }

    public int getNumSynchronizableTBeans() {
        return this.numSynchronizableTBeans;
    }

    public Dispatcher getDispatcher() {
        return this.dispatcher;
    }
}
