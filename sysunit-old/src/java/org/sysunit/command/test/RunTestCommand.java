package org.sysunit.command.test;

public class RunTestCommand
    extends TestCommand {

    public RunTestCommand() {

    }

    public void run(TestServer testServer)
        throws Exception {
        testServer.runTest();
    }
}
