package org.sysunit.command.test;

public class KillCommand
    extends TestCommand {

    public void run(TestServer server) 
        throws Exception {
        System.exit( 1 );
    }
}
