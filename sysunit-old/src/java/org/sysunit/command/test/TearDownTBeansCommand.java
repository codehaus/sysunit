package org.sysunit.command.test;

public class TearDownTBeansCommand
    extends TestCommand {

    public TearDownTBeansCommand() {

    }

    public void run(TestServer testServer)
        throws Exception {
        testServer.tearDownTBeans();
    }
}
