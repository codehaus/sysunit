package org.sysunit.command.master;

import org.sysunit.command.Dispatcher;

public class TestNodeInfo {

    private String name;
    private int numSynchronizableTBeans;
    private Dispatcher dispatcher;

    public TestNodeInfo(String name,
                        int numSynchronizableTBeans,
                        Dispatcher dispatcher) {
        this.name = name;
        this.numSynchronizableTBeans = numSynchronizableTBeans;
        this.dispatcher = dispatcher;
    }

    public String getName() {
        return this.name;
    }

    public int getNumSynchronizableTBeans() {
        return this.numSynchronizableTBeans;
    }

    public Dispatcher getDispatcher() {
        return this.dispatcher;
    }

    public String toString() {
        return "[TestNodeInfo: name=" + this.name
            + "; numSynchronizableTBeans=" + this.numSynchronizableTBeans + "]";
    }
}
