package org.sysunit.command.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UnblockAllCommand
    extends TestCommand {

	private static final Log log = LogFactory.getLog(UnblockAllCommand.class);

    public void run(TestServer testServer)
        throws Exception {
        
        log.info( "* * * * * * unblock all " + this + " on " + testServer.getName() );
        testServer.unblockAll();
        log.info( "* * * * * * unblocked all " + this + " on " + testServer.getName() );
    }
}
