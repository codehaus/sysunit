package org.sysunit;

import java.util.TimerTask;

class Watchdog extends TimerTask {

    private SystemTestCase testCase;

    Watchdog(SystemTestCase testCase) {
        this.testCase = testCase;
    }

    private SystemTestCase getTestCase() {
        return this.testCase;
    }

    public void run() {
        getTestCase().fireWatchdog();
    }

}
