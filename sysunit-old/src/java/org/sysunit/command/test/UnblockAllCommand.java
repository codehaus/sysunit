package org.sysunit.command.test;

public class UnblockAllCommand
    extends TestCommand {

    public void run(TestServer testServer)
        throws Exception {
        testServer.unblockAll();
    }
}
