package org.sysunit.command.test;

import org.sysunit.command.master.TBeansSetUpCommand;

public class SetUpTBeansCommand
    extends TestCommand {

    public SetUpTBeansCommand() {

    }

    public void run(TestServer testServer)
        throws Exception {
        testServer.setUpTBeans();

        getReplyDispatcher().dispatch( new TBeansSetUpCommand( testServer.getName() ) );
    }
}
