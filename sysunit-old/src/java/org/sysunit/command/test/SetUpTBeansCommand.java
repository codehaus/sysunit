package org.sysunit.command.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SetUpTBeansCommand
    extends TestCommand {

    private static final Log log = LogFactory.getLog(SetUpTBeansCommand.class);

    public SetUpTBeansCommand() {
    }

    public void run(TestServer testServer)
        throws Exception {
        testServer.setUpTBeans();
    }
}
