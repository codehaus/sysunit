package org.sysunit.command.test;

import org.sysunit.local.LocalSynchronizer;

public class TestSynchronizer
    extends LocalSynchronizer {

    public TestSynchronizer() {
    }

    protected synchronized boolean shouldUnblock() {
        return false;
    }
}
